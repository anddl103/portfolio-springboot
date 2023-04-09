package com.hybe.larva.dto.faq;

import com.hybe.larva.entity.common.LocaleCodeContents;
import com.hybe.larva.entity.faq.Faq;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@ToString
@Getter
@Builder
public class FaqAddRequest {

    @NotBlank
    private String category;

    private Map<String, LocaleCodeContents> localeCodeContents;

    public Faq toEntity() {
        return Faq.builder()
                .category(category)
                .localeCodeContents(localeCodeContents)
                .build();
    }
}
