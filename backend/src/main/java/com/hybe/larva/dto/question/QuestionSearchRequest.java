package com.hybe.larva.dto.question;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


@ToString(callSuper = true)
@Getter
public class QuestionSearchRequest extends BaseSearchOffsetRequest {

    @Builder
    public QuestionSearchRequest(Integer offset, Integer limit) {
        super(offset, limit);
    }
}
