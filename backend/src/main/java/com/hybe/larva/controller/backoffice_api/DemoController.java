package com.hybe.larva.controller.backoffice_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.DemoRequest;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.nfc_card.NfcCardAddRequest;
import com.hybe.larva.dto.nfc_card.NfcCardDetailResponse;
import com.hybe.larva.dto.product_key.ProductKeyAddRequest;
import com.hybe.larva.dto.product_key.ProductKeyResponse;
import com.hybe.larva.dto.product_key.ProductKeyValidateRequest;
import com.hybe.larva.dto.product_order.ProductOrderAddRequest;
import com.hybe.larva.dto.product_order.ProductOrderResponse;
import com.hybe.larva.entity.album.Album;
import com.hybe.larva.entity.album.AlbumCard;
import com.hybe.larva.entity.album.AlbumRepoExt;
import com.hybe.larva.enums.ServiceRegion;
import com.hybe.larva.service.NfcCardService;
import com.hybe.larva.service.ProductKeyService;
import com.hybe.larva.service.ProductOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static com.hybe.larva.enums.UserRole.ROLES.*;

@Slf4j
@Api(tags = "Demo")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/demo")
@RestController
public class DemoController {

    private final ProductKeyService productKeyService;
    private final ProductOrderService productOrderService;
    private final NfcCardService nfcCardService;
    private final AlbumRepoExt albumRepoExt;

    /**
     * nfcCard 및 product Order 생성
     */
    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "nfcCard 및 product Order 생성",
            notes = "nfcCard 및 product Order 생성한다."
    )
    @Secured({PRODUCT_MANAGER})
    @PostMapping("/ready")
    public CommonResult nfcCardAndProductOrderProcess(
            @RequestBody @Valid DemoRequest request
    ) {
        String artistId = request.getArtistId();
        String uid = request.getUid();
        long quantity = 1;
        int percent = request.getPercent();

        List<Album> albums = albumRepoExt.findAllByArtistId(artistId);
        // 앨범 내 % 비율 nfc_card 주문
        for (Album album : albums) {
            int i = 0;
            // 앨범의 카드 구하기
            List<AlbumCard> cards = album.getCards();

//            CardSearchRequest cardRequest = CardSearchRequest.builder()
//                    .offset(0).limit(-1)
//                    .albumId(album.getId())
//                    .build();
//            Page<CardResponse> cardData = cardService.searchCard(cardRequest);
//            List<CardResponse> cards = cardData.getContent();
            int size = cards.size();
            int length = Math.round((size * percent) / 100);

            for (AlbumCard albumCard : cards) {
                i++;
                if (length < i) {
                    continue;
                }

                /**
                 * nfc Card 등록
                 */
                List<String> cardIds = new ArrayList();
                cardIds.add(albumCard.getId());
                NfcCardDetailResponse nfcCard = nfcCardService.addNfcCard(NfcCardAddRequest.builder()
                        .albumId(album.getId())
                        .comment("자동 등록")
                        .cardIds(cardIds).build());

                /**
                 * product Order 등록
                 */
                ProductOrderResponse productOrder = productOrderService.addProductOrder(ProductOrderAddRequest.builder()
                        .quantity(quantity)
                        .nfcCardId(nfcCard.getId())
                        .comment("자동 생성")
                        .region(ServiceRegion.GLOBAL)
                        .build());

                /**
                 *  productKey 생성 - 검증 - 등록 처리
                 */
                ProductKeyAddRequest productKeyAddRequest = ProductKeyAddRequest.builder()
                        .productOrderId(productOrder.getId())
                        .nfcCardId(productOrder.getNfcCardId())
                        .build();
                ProductKeyResponse data = productKeyService.addProductKey(productKeyAddRequest);

                ProductKeyValidateRequest productKeyValidateRequest = ProductKeyValidateRequest.builder()
                        .code(data.getCode())
                        .productOrderId(data.getProductOrderId())
                        .nfcCardId(data.getNfcCardId())
                        .build();

                productKeyService.assignProductKeyByCode(productKeyValidateRequest);
                productKeyService.verifyProductKeyByCode(productKeyValidateRequest, true);
                productKeyService.tagProductKey(data.getCode(), uid);

            };
        }
        return ResponseHelper.newSuccessResult();
    }
}
