package com.hybe.larva.service;


import com.hybe.larva.dto.product_key.*;
import com.hybe.larva.dto.product_key_history.ProductKeyHistoryAddRequest;
import com.hybe.larva.entity.album.Album;
import com.hybe.larva.entity.album.AlbumCard;
import com.hybe.larva.entity.album.AlbumRepoExt;
import com.hybe.larva.entity.nfc_card.NfcCard;
import com.hybe.larva.entity.nfc_card.NfcCardRepoExt;
import com.hybe.larva.entity.product_key.ProductKey;
import com.hybe.larva.entity.product_key.ProductKeyRepoExt;
import com.hybe.larva.entity.product_key_history.ProductKeyHistory;
import com.hybe.larva.entity.product_key_history.ProductKeyHistoryRepoExt;
import com.hybe.larva.entity.product_order.ProductOrder;
import com.hybe.larva.entity.product_order.ProductOrderRepoExt;
import com.hybe.larva.entity.user_album.UserAlbum;
import com.hybe.larva.entity.user_album.UserAlbumCard;
import com.hybe.larva.entity.user_album.UserAlbumRepoExt;
import com.hybe.larva.entity.user_artist.UserArtist;
import com.hybe.larva.entity.user_artist.UserArtistRepoExt;
import com.hybe.larva.enums.ProductOrderStatus;
import com.hybe.larva.exception.*;
import com.hybe.larva.util.Aes256;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductKeyService {


    private final AlbumRepoExt albumRepoExt;
    private final NfcCardRepoExt nfcCardRepoExt;
    private final ProductOrderRepoExt productOrderRepoExt;
    private final ProductKeyRepoExt productKeyRepoExt;
    private final ProductKeyHistoryRepoExt productKeyHistoryRepoExt;

    private final UserArtistRepoExt userArtistRepoExt;
    private final UserAlbumRepoExt userAlbumRepoExt;




    public ProductKeyResponse addProductKey(ProductKeyAddRequest request) {

        // 등록 전 생성 제한된 주문인지 확인 필요
        ProductOrder productOrder = productOrderRepoExt.findById(request.getProductOrderId());

        if (ProductOrderStatus.COMPLETE.equals(productOrder.getStatus()) ||
            ProductOrderStatus.CANCELLED.equals(productOrder.getStatus())) {
            throw new InvalidProductOrderException("Orders that have already been completed or canceled");
        }

        byte[] secretKey = Aes256.genSecretKey();
        String uniqueCode = ProductKey.generateUniqueCode();
        String code = uniqueCode+":"+Aes256.encrypt(secretKey, uniqueCode);

        ProductKey productKey = request.toEntity(secretKey, uniqueCode);

        productKey = productKeyRepoExt.insert(productKey);

        // product key history
        addProductKeyHistory(productKey, "기기정보", "Product key 생성");

        return new ProductKeyResponse(productKey, code);
    }

    /**
     * nfc 카드 태깅 시 동작
     * user_album, user_card, user_artist, product_key_history 생성
     * user_info point update, user_point_history 생성
     * user_card 에 album에 포함 된 card 전체 등록
     */
    //@Transactional(value = "mongoTransactionManager")
    public ProductKeyTagResponse tagProductKey(String code, String uid) {

        String userUid = uid;
        String[] result = code.split(":");

        ProductKey productKey = productKeyRepoExt.findByCode(result[0]);
        byte[] secretKey = productKey.getSecretKey();
        String checkCode = Aes256.decrypt(secretKey, result[1]);

        boolean duplicated = false;

        // 검증 실패
        if (userUid == null || userUid.isEmpty()) {
            addProductKeyHistory(productKey, "기기정보", "실패:Not an authenticated user");
            throw new AuthException("Not an authenticated user");
        }  else if (!result[0].equals(checkCode)) {
            // history 추가
            addProductKeyHistory(productKey, "기기정보", "실패:This is not correct code");
            throw new InvalidProductKeyException("This is not correct code");
        } else if (Boolean.FALSE.equals(productKey.isAssigned())) {
            addProductKeyHistory(productKey, "기기정보", "실패:This is an unassigned code");
            throw new AssignedProductKeyException("This is an unassigned code");
        } else if (Boolean.FALSE.equals(productKey.isVerified())) {
            addProductKeyHistory(productKey, "기기정보", "실패:This is the code that has not been verified");
            throw new VerifiedProductKeyException("This is the code that has not been verified");
        } else if (productKey.isTagged()) {
            if (productKey.getUid().equals(userUid)) {
                // duplicate
                duplicated = true;
            } else {
                addProductKeyHistory(productKey, "기기정보", "실패:This code is already tagged");
                throw new TaggedProductKeyException("This code is already tagged");
            }
        }

        productKey = productKey.makeUse(userUid);
        productKey = productKeyRepoExt.save(productKey);

        // nfc card 정보로 user_album, user_card 등록하기, user_artist, user point 추가
        NfcCard nfcCard = nfcCardRepoExt.findById(productKey.getNfcCardId());

        ProductKeyRegister register = new ProductKeyRegister();

        String artistId = nfcCard.getArtistId();

        UserArtist userArtist = userArtistRepoExt.findByArtistId(artistId, userUid);

        String albumId = nfcCard.getAlbumId();
        UserAlbum userAlbum = userAlbumRepoExt.findByAlbumIdCheck(albumId, userUid);

        if (Boolean.TRUE.equals(duplicated)) {

            if (register.getUserArtistId() == null) {
                register.setUserArtistId(userArtist.getId());
            }

            if (register.getUserAlbumId() == null) {
                register.setUserAlbumId(userAlbum.getId());
            }

        } else {

            /**
             * 유저 아티스트 등록
             */
            if (userArtist == null) {
                userArtist = UserArtist.builder().artistId(artistId).uid(userUid).build();
                userArtist = userArtistRepoExt.save(userArtist);
            }

            Album album = albumRepoExt.findById(albumId);



            // 유저 앨범에서 전체 카드 수 사용
            List<AlbumCard> cards = album.getCards();


            if (userAlbum == null) {

//                Album album = albumRepoExt.findById(albumId);
                userAlbum = UserAlbum.builder()
                        .uid(userUid)
                        .userArtistId(userArtist.getId())
                        .albumId(albumId)
                        .rewarded(false)
                        .build();
                userAlbum = userAlbumRepoExt.save(userAlbum);
            }


            for (AlbumCard card : cards) {
                String cardId = card.getId();

                if (nfcCard.getCardIds().contains(cardId)) {

                    UserAlbumCard userAlbumCard = null;

                    /**
                     * 유저 카드 등록
                     * nfc Card 의 카드 만 등록처리
                     */
                    if (userAlbum.getCards() != null && userAlbum.getCards().size() > 0) {
                        userAlbumCard =
                                userAlbum.getCards().stream()
                                        .filter(c -> c.getCardId().equals(cardId)).findFirst().orElse(null);
                    }


                    if (userAlbumCard != null) {
                        // 수정 필요. 기존 에 수집 안된 카드는 collected : true 처리
                        log.info("###############################################");
                        userAlbum = userAlbumRepoExt.incCollectCount(userUid, userAlbum.getId(), userAlbumCard.getId());
                    } else {
                        userAlbumCard = userAlbumCard.builder()
                                .id(UUID.randomUUID().toString())
                                .userArtistId(userArtist.getId())
                                .userAlbumId(userAlbum.getId())
                                .uid(userUid)
                                .cardId(cardId)
                                .collectCount(1)
                                .favorite(false)
                                .updatedFlag(false)
                                .newFlag(false)
                                .createdAt(LocalDateTime.now())
                                .build();



                        userAlbum = userAlbumRepoExt.updateUserAlbumCard(userUid, userAlbum.getId(), userAlbumCard);
                    }
                    // 앨범 별 카드 포인트적립
                    // 카드별 포인트 적립 일 경우 로직 변경 필요
                }
            }

            /**
             * 유저 앨범 등록/수정
             */
            long count = userAlbum.getCards().size();

            userAlbum = userAlbum.completed((cards.size() <= count));
            userAlbum = userAlbumRepoExt.save(userAlbum);

            if (register.getUserArtistId() == null) {
                register.setUserArtistId(userArtist.getId());
            }

            if (register.getUserAlbumId() == null) {
                register.setUserAlbumId(userAlbum.getId());
            }

            // product key history
            addProductKeyHistory(productKey, "기기정보", "성공");
        }
        return new ProductKeyTagResponse(productKey, code, duplicated, register);
    }


    @Retryable(OptimisticLockingFailureException.class)
    public void deleteProductKey(String productKeyId) {
//        productKeyRepoExt.deleteById(productKeyId);
        ProductKey productKey = productKeyRepoExt.findById(productKeyId).delete();
        productKeyRepoExt.save(productKey);
    }

    public ProductKeyResponse verifyProductKeyByCode(ProductKeyValidateRequest request, boolean verified) {

        String code = request.getCode();
        String[] result = code.split(":");

        // 코드 검증.
        ProductKey productKey = productKeyRepoExt.verifyByCode(result[0]);

        // 주문 id, nfcCard Id 확인
        if (!productKey.getProductOrderId().equals(request.getProductOrderId()) ||
                !productKey.getNfcCardId().equals(request.getNfcCardId())) {
            throw new InvalidProductKeyException("This is not the code for the correct order.");
        }

        byte[] secretKey = productKey.getSecretKey();

        String checkCode = Aes256.decrypt(secretKey, result[1]);

        // 코드 검증 실패
        if (!result[0].equals(checkCode)) {
            throw new InvalidProductKeyException("This is not correct code");
        }

        productKey = productKey.verified(verified);
        productKey = productKeyRepoExt.save(productKey);

        ProductOrder productOrder = productOrderRepoExt.findById(productKey.getProductOrderId());
        long verifyCount = productKeyRepoExt.findByProductOderVerify(productKey.getProductOrderId());
        if (productOrder.getQuantity() <= verifyCount) {
            productOrder = productOrder.complete();
            productOrderRepoExt.save(productOrder);
        }
        return new ProductKeyResponse(productKey);
    }

    public ProductKeyResponse assignProductKeyByCode(ProductKeyValidateRequest request) {


        String code = request.getCode();

        String[] result = code.split(":");

        ProductKey productKey = productKeyRepoExt.assignByCode(result[0]);

        // 주문 id, nfcCard Id 확인
        if (!productKey.getProductOrderId().equals(request.getProductOrderId()) ||
            !productKey.getNfcCardId().equals(request.getNfcCardId())) {
            throw new InvalidProductKeyException("This is not the code for the correct order.");
        }

        String getCode = productKey.getCode();

        byte[] secretKey = productKey.getSecretKey();

        String checkCode = Aes256.decrypt(secretKey, result[1]);

        log.info("getCode : " + getCode);
        log.info("checkCode : " + checkCode);

        // 코드 검증 실패
        if (!result[0].equals(checkCode)) {
            throw new InvalidProductKeyException("This is not correct code");
        }

        productKey = productKey.assigned();
        productKey = productKeyRepoExt.save(productKey);

        return new ProductKeyResponse(productKey);
    }


    // product key history
    private void addProductKeyHistory(ProductKey productKey, String deviceInfo, String failedReason) {
        ProductKeyHistory productKeyHistory =
                ProductKeyHistoryAddRequest.builder()
                        .deviceInfo(deviceInfo)
                        .failedReason(failedReason)
                        .build()
                        .toEntity(productKey);

        productKeyHistoryRepoExt.insert(productKeyHistory);
    }
}
