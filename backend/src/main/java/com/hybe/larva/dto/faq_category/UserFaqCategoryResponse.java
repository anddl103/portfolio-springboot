package com.hybe.larva.dto.faq_category;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.faq_category.FaqCategory;
import com.hybe.larva.entity.faq_category.LocaleCodeSubject;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@ToString(callSuper = true)
@Getter
public class UserFaqCategoryResponse extends BaseResponse {

    private String subject;

    private String comment;

    public UserFaqCategoryResponse(FaqCategory faqCategory, CacheUtil cacheUtil, String lang) {
        super(faqCategory);

        this.subject = faqCategory.getLocaleCodeSubject().get(cacheUtil.getLanguage(lang)).getSubject();
        this.comment = faqCategory.getComment();
    }
}
