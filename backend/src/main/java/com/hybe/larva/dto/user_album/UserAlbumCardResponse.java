package com.hybe.larva.dto.user_album;

import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.entity.view_user_card.ViewUserCard;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@ToString(callSuper = true)
@Getter
public class UserAlbumCardResponse {

    private String id;

    private String refId;

    private boolean favorite;

    private int collectCount;

    private boolean newFlag;

    private boolean updatedFlag;

    private List<String> members;

    private CardContents contents;

    private int position;

    private boolean collected;

    private LocalDateTime createdAt;

    private String createdBy;

    public UserAlbumCardResponse(ViewUserCard viewUserCard) {
        this.refId = viewUserCard.getCardId();
        this.position = viewUserCard.getPosition();
        this.members = viewUserCard.getMembers();
        this.contents = viewUserCard.getContents();

        this.collected = (viewUserCard.getCollectCount() > 0 ? true : false);

        this.id = viewUserCard.getUserCardId();
        this.favorite = viewUserCard.isFavorite();
        this.collectCount = viewUserCard.getCollectCount();
        this.newFlag = viewUserCard.isNewFlag();
        this.updatedFlag = viewUserCard.isUpdatedFlag();
        this.createdAt = viewUserCard.getCreatedAt();
    }
}
