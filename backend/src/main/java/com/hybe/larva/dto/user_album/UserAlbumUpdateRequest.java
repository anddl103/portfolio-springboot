package com.hybe.larva.dto.user_album;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class UserAlbumUpdateRequest {

    private String uid;

    private boolean completed;

    private boolean rewarded;
}
