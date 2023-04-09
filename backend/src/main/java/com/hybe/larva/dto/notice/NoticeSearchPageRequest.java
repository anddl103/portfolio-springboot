package com.hybe.larva.dto.notice;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import com.hybe.larva.dto.common.BaseSearchRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class NoticeSearchPageRequest extends BaseSearchOffsetRequest {

    private final String keyword;

    @Builder
    public NoticeSearchPageRequest(LocalDateTime from, LocalDateTime to, Integer offset, Integer limit, String keyword) {
        super(from, to, offset, limit);
        this.keyword = keyword;
    }
}
