package com.hybe.larva.dto.user_info;

import com.hybe.larva.dto.common.BaseSearchRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class UserInfoProductKeyHistorySearchRequest extends BaseSearchRequest {

//    private final String keyword;

    @Builder
    public UserInfoProductKeyHistorySearchRequest(LocalDateTime from, LocalDateTime to, Integer page, Integer pageSize) {
        super(from, to, page, pageSize);
    }
}
