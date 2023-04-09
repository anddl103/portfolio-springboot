package com.hybe.larva.dto.faq;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class FaqSearchRequest extends BaseSearchOffsetRequest {

    @Builder
    public FaqSearchRequest(Integer offset, Integer limit) {
        super(offset, limit);
    }
}
