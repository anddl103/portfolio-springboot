package com.hybe.larva.dto.user_artist;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class UserArtistSearchRequest extends BaseSearchOffsetRequest {

    @Builder
    public UserArtistSearchRequest(Integer offset, Integer limit) {
        super(offset, limit);
    }
}
