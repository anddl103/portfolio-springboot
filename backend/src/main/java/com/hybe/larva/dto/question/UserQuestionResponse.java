package com.hybe.larva.dto.question;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.question.Question;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class UserQuestionResponse extends BaseResponse {

    private String title;

    private String content;

    private String answer;

    private LocalDateTime answeredAt;

    private String answeredBy;

    public UserQuestionResponse(Question question) {
        super(question);
        this.title = question.getTitle();
        this.content = question.getContent();
        this.answer = question.getAnswer();
        this.answeredAt = question.getAnsweredAt();
        this.answeredBy = question.getAnsweredBy();
    }
}
