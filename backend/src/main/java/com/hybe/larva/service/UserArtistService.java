package com.hybe.larva.service;

import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.dto.user_artist.UserArtistSearchRequest;
import com.hybe.larva.dto.user_artist.UserArtistSearchResponse;
import com.hybe.larva.entity.user_artist.UserArtist;
import com.hybe.larva.entity.user_artist.UserArtistAggregation;
import com.hybe.larva.entity.user_artist.UserArtistRepoExt;
import com.hybe.larva.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserArtistService {

    private final UserArtistRepoExt userArtistRepoExt;
    private final CacheUtil cacheUtil;

    public UserArtistSearchResponse getUserArtist(String userArtistId) {
        UserArtistAggregation userArtist = userArtistRepoExt.findByIdDetail(userArtistId);
        return new UserArtistSearchResponse(userArtist, cacheUtil);
    }

    public Page<UserArtistSearchResponse> searchUserArtist(UserArtistSearchRequest request) {
        final Criteria criteria = Criteria.where(UserArtist.DELETED).ne(true);
        criteria.and(UserArtist.UID).is(CommonUser.getUid());

        return userArtistRepoExt.search(criteria, request.getPageable())
                .map(userArtist -> new UserArtistSearchResponse(userArtist, cacheUtil));
    }
}
