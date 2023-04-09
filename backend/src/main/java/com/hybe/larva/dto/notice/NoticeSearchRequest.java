package com.hybe.larva.dto.notice;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import lombok.Builder;

public class NoticeSearchRequest extends BaseSearchOffsetRequest {

    @Builder
    public NoticeSearchRequest(Integer offset, Integer limit) {
        super(offset, limit);
    }
}
