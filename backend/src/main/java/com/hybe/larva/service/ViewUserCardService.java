package com.hybe.larva.service;

import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.dto.view_user_card.ViewUserCardResponse;
import com.hybe.larva.dto.view_user_card.ViewUserCardSearchRequest;
import com.hybe.larva.entity.user_album.UserAlbumRepoExt;
import com.hybe.larva.entity.view_user_card.ViewUserCard;
import com.hybe.larva.entity.view_user_card.ViewUserCardRepoExt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ViewUserCardService {

    private final ViewUserCardRepoExt viewUserCardRepoExt;
    private final UserAlbumRepoExt userAlbumRepoExt;

    public void createViewUserCard() {

        try {
            viewUserCardRepoExt.viewCreate();
        } catch (UncategorizedMongoDbException e) {
            log.info("  ############## 이미 생성되어 있습니다. ");
        } catch (Exception e) {
            log.info(" view error : " + e.getMessage());
        }
    }

    public ViewUserCardResponse getUserCard(String userCardId) {
        final Criteria criteria = new Criteria();
        criteria.and(ViewUserCard.USER_CARD_ID).is(userCardId);
        criteria.and(ViewUserCard.UID).is(CommonUser.getUid());
        criteria.and(ViewUserCard.COLLECT_COUNT).gte(1);
        ViewUserCard viewUserCard = viewUserCardRepoExt.findByIdDetail(criteria);
        return new ViewUserCardResponse(viewUserCard);
    }

    public ViewUserCardResponse getUserCard(String userCardId, boolean isCollected) {
        final Criteria criteria = new Criteria();
        if (Boolean.TRUE.equals(isCollected)) {
            criteria.and(ViewUserCard.USER_CARD_ID).is(userCardId);
        } else {
            criteria.and(ViewUserCard.CARD_ID).is(userCardId);
        }
        criteria.and(ViewUserCard.UID).is(CommonUser.getUid());

        ViewUserCard viewUserCard = viewUserCardRepoExt.findByIdDetail(criteria);

        return new ViewUserCardResponse(viewUserCard);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public ViewUserCardResponse updateUserCardFavorite(String userCardId, Boolean favorite) {
        if (favorite != null) {
            userAlbumRepoExt.updateUserAlbumCardFavorite(userCardId, CommonUser.getUid(), favorite);
        }

        return getUserCard(userCardId);
    }


    public Page<ViewUserCardResponse> searchUserCard(ViewUserCardSearchRequest request) {

        final Criteria criteria = Criteria.where(ViewUserCard.UID).is(CommonUser.getUid());
        criteria.and(ViewUserCard.COLLECT_COUNT).gte(1);

        if (request.getFavorite() != null) {
            criteria.and(ViewUserCard.FAVORITE).is(request.getFavorite());
        }

        if (request.getArtistId() != null) {
            criteria.and(ViewUserCard.USER_ARTIST_ID).is(request.getArtistId());
        }

        return viewUserCardRepoExt.search(criteria, request.getPageable())
                .map(ViewUserCardResponse::new);
    }

}
