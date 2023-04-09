package com.hybe.larva.entity.question;

import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.dto.question.QuestionUpdateRequest;
import com.hybe.larva.entity.common.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Optional;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "questions")
public class Question extends BaseEntity {

    private String localeCode;

    private String title;

    private String content;

    private String answer;

    private LocalDateTime answeredAt;

    private String answeredBy;

    @Builder
    public Question (String localeCode, String title, String content, String answer) {
        this.localeCode = localeCode;
        this.title = title;
        this.content = content;
        this.answer = answer;
    }


    public Question update(QuestionUpdateRequest request) {

        Optional.ofNullable(request.getAnswer()).ifPresent(v -> {
            this.answer = v;
            this.answeredAt = LocalDateTime.now();
            this.answeredBy = CommonUser.getUid();
        });

        return this;
    }

    public Question delete() {
        this.deleted = true;
        return this;
    }
}
