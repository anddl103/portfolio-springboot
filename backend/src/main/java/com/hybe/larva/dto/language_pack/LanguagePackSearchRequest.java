package com.hybe.larva.dto.language_pack;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class LanguagePackSearchRequest extends BaseSearchOffsetRequest {

    private final String keyword;

    @Builder
    public LanguagePackSearchRequest(LocalDateTime from, LocalDateTime to, Integer offset, Integer limit, String keyword) {
        super(from, to, offset, limit);
        this.keyword = keyword;
    }
}
