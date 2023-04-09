package com.hybe.larva.dto.view_user_card;

import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.entity.view_user_card.ViewUserCard;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString(callSuper = true)
@Getter
public class ViewUserCardResponse {

    private final String id;

    private  LocalDateTime createdAt;

    private final String uid;

    private final String refId;

    private final boolean favorite;

    private boolean newFlag;

    private boolean updatedFlag;

    private int collectCount;

    private List<String> members;

    private CardContents contents;

    public ViewUserCardResponse(ViewUserCard viewUserCard) {

        this.id = viewUserCard.getUserCardId();
//        this.createdAt = viewUserCard.getCreatedAt();
        this.uid = viewUserCard.getUid();
        this.refId = viewUserCard.getCardId();
        this.favorite = viewUserCard.isFavorite();
        this.newFlag = viewUserCard.isNewFlag();
        this.updatedFlag = viewUserCard.isUpdatedFlag();
        this.collectCount = viewUserCard.getCollectCount();
        this.members = viewUserCard.getMembers();
        this.contents = viewUserCard.getContents();

    }


}
