package com.hybe.larva.dto.question;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.question.Question;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class QuestionResponse extends BaseResponse {

    private String localeCode;

    private String title;

    private String content;

    private String answer;

    private LocalDateTime answeredAt;

    private String answeredBy;

    public QuestionResponse(Question question) {
        super(question);
        this.localeCode = question.getLocaleCode();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.answer = question.getAnswer();
        this.answeredAt = question.getAnsweredAt();
        this.answeredBy = question.getAnsweredBy();
    }
}
