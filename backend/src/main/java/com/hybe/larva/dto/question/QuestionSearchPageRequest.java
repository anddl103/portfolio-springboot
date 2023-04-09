package com.hybe.larva.dto.question;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class QuestionSearchPageRequest extends BaseSearchOffsetRequest {

    @Builder
    public QuestionSearchPageRequest(LocalDateTime from, LocalDateTime to, Integer offset, Integer limit) {
        super(from, to, offset, limit);
    }
}
