package com.hybe.larva.dto.locale_code;

import com.hybe.larva.entity.locale_code.LocaleCodes;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@ToString
@Getter
@Builder
public class LocaleCodeAddRequest {

    @NotBlank
    private String code;

    private String comment;

    public LocaleCodes toEntity() {
        return LocaleCodes.builder()
                .code(code)
                .comment(comment)
                .build();
    }
}
