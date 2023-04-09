package com.hybe.larva.dto.policy;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import com.hybe.larva.dto.common.BaseSearchRequest;
import lombok.Builder;

import java.time.LocalDateTime;

public class PolicySearchRequest extends BaseSearchOffsetRequest {

    @Builder
    public PolicySearchRequest(LocalDateTime from, LocalDateTime to, Integer offset, Integer limit) {
        super(from, to, offset, limit);
    }
}
