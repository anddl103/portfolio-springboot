package com.hybe.larva.service;

import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.dto.user_album.*;
import com.hybe.larva.entity.user_album.UserAlbum;
import com.hybe.larva.entity.user_album.UserAlbumAggregation;
import com.hybe.larva.entity.user_album.UserAlbumRepoExt;
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
public class UserAlbumService {

    private final UserAlbumRepoExt userAlbumRepoExt;
    private final ViewUserCardRepoExt viewUserCardRepoExt;
    private final CacheUtil cacheUtil;

    public UserAlbumSearchResponse getUserAlbum(String userAlbumId) {

        UserAlbumAggregation userAlbum = userAlbumRepoExt.findByIdDetail(userAlbumId);

        return new UserAlbumSearchResponse(userAlbum, cacheUtil);
    }

    public Page<UserAlbumCardResponse> getUserAlbumCard(UserAlbumCardSearchRequest request) {

        final Criteria criteria = Criteria.where(ViewUserCard.UID).is(CommonUser.getUid());
        criteria.and(ViewUserCard.USER_ALBUM_ID).is(request.getUserAlbumId());

        return viewUserCardRepoExt.search(criteria, request.getPageable())
                .map(UserAlbumCardResponse::new);

    }

    public UserAlbumRewardResponse getUserAlbumReward(String userAlbumId) {

        UserAlbumAggregation userAlbum = userAlbumRepoExt.findByIdDetail(userAlbumId);

        return new UserAlbumRewardResponse(userAlbum, cacheUtil);
    }


    public Page<UserAlbumSearchResponse> searchUserAlbum(UserAlbumSearchRequest request) {
        final Criteria criteria = new Criteria(UserAlbum.DELETED).ne(true);
        criteria.and(UserAlbum.UID).is(CommonUser.getUid());

        if (request.getArtistId() != null) {
            criteria.and(UserAlbum.USER_ARTIST_ID).is(request.getArtistId());
        }

        return userAlbumRepoExt.search(criteria, request.getPageable())
                .map(userAlbum -> new UserAlbumSearchResponse(userAlbum, cacheUtil));
    }


    @Retryable(OptimisticLockingFailureException.class)
    public UserAlbumRewardResponse updateUserAlbumRewarded(String userAlbumId) {
        // albumId 로 user_album 조회
        UserAlbum userAlbum = userAlbumRepoExt.findByAlbumId(userAlbumId, CommonUser.getUid());

        if (Boolean.TRUE.equals(userAlbum.isCompleted())) {
            userAlbum = userAlbum.rewarded(true);
            userAlbumRepoExt.save(userAlbum);
        }

        return getUserAlbumReward(userAlbumId);
    }
}
