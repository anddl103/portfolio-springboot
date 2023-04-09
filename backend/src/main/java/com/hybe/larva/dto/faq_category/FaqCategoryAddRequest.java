package com.hybe.larva.dto.faq_category;

import com.hybe.larva.entity.faq_category.FaqCategory;
import com.hybe.larva.entity.faq_category.LocaleCodeSubject;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@ToString
@Getter
@Builder
public class FaqCategoryAddRequest {

    private Map<String, LocaleCodeSubject> localeCodeSubject;

    private String comment;

    public FaqCategory toEntity() {
        return FaqCategory.builder()
                .localeCodeSubject(localeCodeSubject)
                .comment(comment)
                .build();
    }
}
