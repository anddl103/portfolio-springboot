package com.hybe.larva.dto.user_artist;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(callSuper = true)
@Getter
@Builder
public class UserArtistMemberResponse {

    private String id;

    private String name;
}
