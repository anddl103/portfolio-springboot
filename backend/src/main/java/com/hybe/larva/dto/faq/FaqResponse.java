package com.hybe.larva.dto.faq;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.common.LocaleCodeContents;
import com.hybe.larva.util.CacheUtil;
import com.hybe.larva.enums.Category;
import com.hybe.larva.entity.faq.Faq;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@ToString(callSuper = true)
@Getter
public class FaqResponse extends BaseResponse {

    private String category;

    private Map<String, LocaleCodeContents> localeCodeContents;

    public FaqResponse(Faq faq) {
        super(faq);

        this.category = faq.getCategory();
        this.localeCodeContents = faq.getLocaleCodeContents();


    }

}
