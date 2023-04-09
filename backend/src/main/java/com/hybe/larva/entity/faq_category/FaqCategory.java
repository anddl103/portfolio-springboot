package com.hybe.larva.entity.faq_category;


import com.hybe.larva.dto.faq_category.FaqCategoryUpdateRequest;
import com.hybe.larva.entity.common.BaseEntity;
import com.hybe.larva.entity.common.LocaleCodeContents;
import com.hybe.larva.entity.faq.Faq;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Optional;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "faqCategories")
public class FaqCategory extends BaseEntity {

    public static final String COMMENT = "comment";

    private Map<String, LocaleCodeSubject> localeCodeSubject;

    private String comment;

    @Builder
    public FaqCategory(Map<String, LocaleCodeSubject> localeCodeSubject, String comment) {
        this.localeCodeSubject = localeCodeSubject;
        this.comment = comment;
    }

    public FaqCategory update(FaqCategoryUpdateRequest request) {
        Optional.ofNullable(request.getLocaleCodeSubject()).ifPresent(v -> this.localeCodeSubject = v);
        Optional.ofNullable(request.getComment()).ifPresent(v -> this.comment = v);

        return this;
    }

    public FaqCategory delete() {
        this.deleted = true;
        return this;
    }

}
