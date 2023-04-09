package com.hybe.larva.dto.user_info;

import com.hybe.larva.dto.common.BaseSearchRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class UserInfoArtistSearchRequest extends BaseSearchRequest {

    @Builder
    public UserInfoArtistSearchRequest(LocalDateTime from, LocalDateTime to, Integer page, Integer pageSize) {
        super(from, to, page, pageSize);
    }

}
