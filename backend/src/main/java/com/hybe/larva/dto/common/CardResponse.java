package com.hybe.larva.dto.common;

import com.hybe.larva.entity.album.AlbumCard;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class CardResponse {

    private String id;

    private List<String> tags;

    private String thumbnailKey;

    public CardResponse(AlbumCard albumCard) {
        this.id = albumCard.getId();
        this.tags = albumCard.getTags();
        this.thumbnailKey = albumCard.getContents().getThumbnailKey();
    }
}
