package com.hybe.larva.dto.album;

import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.entity.album.AlbumCard;
import com.hybe.larva.enums.OperationType;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@Getter
public class CardResponse {

    private final String id;

    private final int position;

    // title, multi-language
    private final List<String> tags;

    private List<String> members;

    private final CardContents contents;

    private final OperationType operationType;

    public CardResponse(AlbumCard card) {

        this.id = card.getId();
        this.position = card.getPosition();
        this.tags = card.getTags();
        this.members = card.getMembers();
        this.contents = card.getContents();
        this.operationType = OperationType.NONE;
    }
}
