package com.hybe.larva.dto.faq;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import com.hybe.larva.dto.common.BaseSearchRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class FaqSearchPageRequest extends BaseSearchOffsetRequest {

    private final String keyword;
    private final String category;

    @Builder
    public FaqSearchPageRequest(LocalDateTime from, LocalDateTime to, Integer offset, Integer limit, String keyword, String category) {
        super(from, to, offset, limit);
        this.keyword = keyword;
        this.category = category;
    }
}
