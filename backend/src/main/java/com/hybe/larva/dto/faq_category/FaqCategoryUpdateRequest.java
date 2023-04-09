package com.hybe.larva.dto.faq_category;

import com.hybe.larva.entity.common.LocaleCodeContents;
import com.hybe.larva.entity.faq_category.LocaleCodeSubject;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@ToString
@Getter
@Builder
public class FaqCategoryUpdateRequest {

    private Map<String, LocaleCodeSubject> localeCodeSubject;

    private String comment;
}
