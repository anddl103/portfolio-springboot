package com.hybe.larva.dto.locale_code;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class LocaleCodeUpdateRequest {

    private String code;

    private String comment;
}
