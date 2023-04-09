package com.hybe.larva.dto.user_info;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import com.hybe.larva.dto.common.BaseSearchRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class UserInfoSearchRequest extends BaseSearchOffsetRequest {

    @Builder
    public UserInfoSearchRequest(LocalDateTime from, LocalDateTime to, Integer offset, Integer limit) {
        super(from, to, offset, limit);
    }
}
