package com.hybe.larva.entity.user_album;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@ToString
@Data
@Builder
public class UserAlbumCard {

    private String id;

    private String uid;

    private String cardId;

    private String userArtistId;

    private String userAlbumId;

    private boolean favorite;

    private int collectCount;

    private boolean newFlag;

    private boolean updatedFlag;

    private LocalDateTime createdAt;

}
