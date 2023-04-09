package com.hybe.larva.dto.question;

import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.entity.question.Question;
import com.hybe.larva.util.CacheUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class QuestionAddRequest {

    private String title;

    private String content;

    public Question toEntity(CacheUtil cacheUtil) {
        return Question.builder()
                .localeCode(cacheUtil.getLanguage())
                .title(title)
                .content(content)
                .answer("")
                .build();
    }
}
