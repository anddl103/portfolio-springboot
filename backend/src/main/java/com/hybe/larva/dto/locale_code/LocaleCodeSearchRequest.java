package com.hybe.larva.dto.locale_code;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import lombok.Builder;

import java.time.LocalDateTime;

public class LocaleCodeSearchRequest extends BaseSearchOffsetRequest {

    @Builder
    public LocaleCodeSearchRequest(LocalDateTime from, LocalDateTime to, Integer offset, Integer limit) {
        super(from, to, offset, limit);
    }
}
