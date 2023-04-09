package com.hybe.larva.entity.faq;

import com.hybe.larva.dto.faq.FaqUpdateRequest;
import com.hybe.larva.entity.common.BaseEntity;
import com.hybe.larva.entity.common.LocaleCodeContents;
import com.hybe.larva.enums.Category;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Optional;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "faqs")
public class Faq extends BaseEntity {

    public static final String TITLE = "title";
    public static final String CATEGORY = "category";
    public static final String LOCALE_CODE_CONTENTS = "localeCodeContents";

    @Indexed
    private String category;

    private Map<String, LocaleCodeContents> localeCodeContents;


    @Builder
    public Faq (String category, Map<String, LocaleCodeContents> localeCodeContents) {
        this.category = category;
        this.localeCodeContents = localeCodeContents;
    }

    public Faq update(FaqUpdateRequest request) {

        Optional.ofNullable(request.getCategory()).ifPresent(v -> this.category = v);
        Optional.ofNullable(request.getLocaleCodeContents()).ifPresent(v -> this.localeCodeContents = v);

        return this;
    }

    public Faq delete() {
        this.deleted = true;
        return this;
    }
}
