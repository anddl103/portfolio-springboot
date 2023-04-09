package com.hybe.larva.dto.user_info;

import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.entity.view_user_card.ViewUserCard;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString(callSuper = true)
@Getter
public class UserInfoCardResponse {

    private final String id;

    private final LocalDateTime createdAt;

    private String cardId;

    private boolean favorite;

    private int collectCount;

    private List<String> members;

    private CardContents contents;

    public UserInfoCardResponse(ViewUserCard userCard) {
//        super(userCard);
        this.favorite = userCard.isFavorite();
        this.collectCount = userCard.getCollectCount();
        this.id = userCard.getUserCardId();
        this.createdAt = userCard.getCreatedAt();
        this.cardId = userCard.getCardId();
        this.members = userCard.getMembers();
        this.contents = userCard.getContents();
    }
}
