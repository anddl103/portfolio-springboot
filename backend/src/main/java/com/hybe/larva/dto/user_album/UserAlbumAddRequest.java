package com.hybe.larva.dto.user_album;

import com.hybe.larva.entity.album.Album;
import com.hybe.larva.entity.user_album.UserAlbum;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@ToString
@Getter
@Builder
public class UserAlbumAddRequest {

    @NotBlank
    private String userUid;

    @NotBlank
    private String albumId;

    private boolean completed;

    public UserAlbum toEntity(Album album) {
        return UserAlbum.builder()
                .uid(userUid)
                .albumId(albumId)
                .completed(completed)
                .build();
    }
}
