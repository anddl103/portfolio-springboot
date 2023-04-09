package com.hybe.larva.dto.faq_category;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.faq_category.FaqCategory;
import com.hybe.larva.entity.faq_category.LocaleCodeSubject;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@ToString(callSuper = true)
@Getter
public class FaqCategoryResponse extends BaseResponse {

    private Map<String, LocaleCodeSubject> localeCodeSubject;

    private String comment;

    public FaqCategoryResponse(FaqCategory faqCategory) {
        super(faqCategory);

        this.localeCodeSubject = faqCategory.getLocaleCodeSubject();
        this.comment = faqCategory.getComment();
    }
}
