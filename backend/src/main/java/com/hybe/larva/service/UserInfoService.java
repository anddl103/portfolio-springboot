package com.hybe.larva.service;


import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.dto.user_info.*;
import com.hybe.larva.entity.product_key_history.ProductKeyHistoryRepoExt;
import com.hybe.larva.entity.user_album.UserAlbum;
import com.hybe.larva.entity.user_album.UserAlbumRepoExt;
import com.hybe.larva.entity.user_artist.UserArtist;
import com.hybe.larva.entity.user_artist.UserArtistRepoExt;
import com.hybe.larva.entity.user_info.UserInfo;
import com.hybe.larva.entity.user_info.UserInfoRepoExt;
import com.hybe.larva.entity.view_user_card.ViewUserCard;
import com.hybe.larva.entity.view_user_card.ViewUserCardRepoExt;
import com.hybe.larva.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInfoService {

    private final UserInfoRepoExt userInfoRepoExt;
    private final ViewUserCardRepoExt viewUserCardRepoExt;
    private final UserAlbumRepoExt userAlbumRepoExt;
    private final UserArtistRepoExt userArtistRepoExt;
    private final ProductKeyHistoryRepoExt productKeyHistoryRepoExt;
    private final CacheUtil cacheUtil;

    public UserInfoResponse addUserInfo(UserInfoAddRequest request) {
        UserInfo userInfo = request.toEntity(CommonUser.getUid());
        userInfo = userInfoRepoExt.insert(userInfo);
        return new UserInfoResponse(userInfo);
    }

    public Page<UserInfoResponse> searchUserInfo(UserInfoSearchRequest request) {
        final Criteria criteria = Criteria.where(UserInfo.DELETED).ne(true);

        if (request.getFrom() != null && request.getTo() != null) {
            criteria.and(UserInfo.CREATED_AT).gte(request.getFrom()).lt(request.getTo());
        }

        return userInfoRepoExt.search(criteria, request.getPageable())
                .map(UserInfoResponse::new);
    }

    public UserInfoResponse getUserInfo(String id) {
        UserInfo userInfo = userInfoRepoExt.findById(id);
        return new UserInfoResponse(userInfo);
    }

    public UserInfoResponse getUserInfoUid(String userUId) {
        UserInfo userInfo = userInfoRepoExt.findByUid(userUId);
        return new UserInfoResponse(userInfo);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public UserInfoResponse updateUserInfoUid(String userInfoId, UserInfoUpdateRequest request) {
        UserInfo userInfo = userInfoRepoExt.findByUid(userInfoId).update(request);
        userInfo = userInfoRepoExt.save(userInfo);
        return new UserInfoResponse(userInfo);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public void deleteUserInfo(String userUId) {
        UserInfo userInfo = userInfoRepoExt.findByUid(userUId).delete();
        userInfoRepoExt.save(userInfo);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public UserInfoResponse updateUserInfoPushUid(String userUid, boolean push) {
        UserInfo userInfo = userInfoRepoExt.findByUid(userUid).updatePush(push);
        userInfo = userInfoRepoExt.save(userInfo);
        return new UserInfoResponse(userInfo);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public UserInfoResponse updateUserInfoOver14YearsOld(String userUid, boolean over14YearsOld) {
        UserInfo userInfo = userInfoRepoExt.findByUid(userUid).updateOver14YearsOld(over14YearsOld);
        userInfo = userInfoRepoExt.save(userInfo);
        return new UserInfoResponse(userInfo);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public UserInfoResponse updateUserInfoPrivacyPolicy(String userUid, boolean privacyPolicy) {
        UserInfo userInfo = userInfoRepoExt.findByUid(userUid).updatePrivacyPolicy(privacyPolicy);
        userInfo = userInfoRepoExt.save(userInfo);
        return new UserInfoResponse(userInfo);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public UserInfoResponse updateUserInfoTermsOfService(String userUid, boolean termsOfService) {
        UserInfo userInfo = userInfoRepoExt.findByUid(userUid).updateTermsOfService(termsOfService);
        userInfo = userInfoRepoExt.save(userInfo);
        return new UserInfoResponse(userInfo);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public UserInfoResponse updateUserInfoWithdrawalUid(String userUid, boolean withdrawal) {
        UserInfo userInfo = userInfoRepoExt.findByUid(userUid).updateWithdrawal(withdrawal);
        userInfo = userInfoRepoExt.save(userInfo);
        return new UserInfoResponse(userInfo);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public UserInfoResponse updateDeviceTokensUid(String deviceToken) {
        UserInfo userInfo = userInfoRepoExt.updateDeviceTokensUid(deviceToken);
        return new UserInfoResponse(userInfo);
    }


    public Page<UserInfoCardResponse> searchUserCard(UserInfoCardSearchRequest request, String uid) {
        final Criteria criteria = new Criteria();
        criteria.and(ViewUserCard.UID).is(uid);

        if (request.getFavorite() != null) {
            criteria.and(ViewUserCard.FAVORITE).is(request.getFavorite());
        }

        if (request.getArtistId() != null) {
//            criteria.and(ViewUserCard.ARTIST_ID).is(request.getArtistId());
        }

        if (request.getAlbumId() != null) {
//            Album album = albumRepoExt.findById(request.getAlbumId());
//            criteria.and(UserCard.CARD_ID).in(album.getCards().stream().map(AlbumCard::getCardId).collect(Collectors.toList()));
        }

        if (request.getKeyword() != null) {
//            criteria.and(UserCard.CARD_SEARCH_TAGS).regex(request.getKeyword());
        }

        return viewUserCardRepoExt.search(criteria, request.getPageable())
                .map(UserInfoCardResponse::new);


    }

    public Page<UserInfoAlbumResponse> searchUserAlbum(UserInfoAlbumSearchRequest request, String uid) {
        final Criteria criteria = new Criteria(UserAlbum.DELETED).ne(true);
        criteria.and(UserAlbum.UID).is(uid);

        if (request.getArtistId() != null) {
            criteria.and(UserAlbum.ARTIST_ID).is(request.getArtistId());
        }

        if (request.getKeyword() != null) {
            criteria.and(UserAlbum.ALBUM_SEARCH_TAGS).regex(request.getKeyword());
        }

        return userAlbumRepoExt.search(criteria, request.getPageable())
                .map(album -> new UserInfoAlbumResponse(album, cacheUtil));
    }

    public Page<UserInfoArtistResponse> searchUserArtist(UserInfoArtistSearchRequest request, String uid) {

                final Criteria criteria = Criteria.where(UserArtist.DELETED).ne(true);
        criteria.and(UserArtist.UID).is(uid);

        return userArtistRepoExt.search(criteria, request.getPageable())
                .map(artist -> new UserInfoArtistResponse(artist, cacheUtil));
    }


    public Page<UserInfoProductKeyHistoryResponse> searchUserProductKeyHistory(UserInfoProductKeyHistorySearchRequest request, String uid) {
        final Criteria criteria = new Criteria();

        return productKeyHistoryRepoExt.searchUid(criteria, request.getPageable(), uid)
                .map(UserInfoProductKeyHistoryResponse::new);
    }
}
