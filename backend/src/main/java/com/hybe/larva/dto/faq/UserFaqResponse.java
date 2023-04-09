package com.hybe.larva.dto.faq;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.common.LocaleCodeContents;
import com.hybe.larva.entity.faq.Faq;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@ToString(callSuper = true)
@Getter
public class UserFaqResponse extends BaseResponse {

    private String category;

    private String subject;

    private String contents;

    public UserFaqResponse(Faq faq, CacheUtil cacheUtil, String lang) {
        super(faq);

        this.category = faq.getCategory();
        if (faq.getLocaleCodeContents() != null) {
            LocaleCodeContents localeCodeContents = faq.getLocaleCodeContents().get(cacheUtil.getLanguage(lang));
            if (localeCodeContents == null) {
                localeCodeContents = faq.getLocaleCodeContents().get(CacheUtil.DEFAULT_COUNTRY_CODE);
            }
            this.subject = localeCodeContents.getSubject();
            this.contents = localeCodeContents.getContents();
        }
    }

}
