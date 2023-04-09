package com.hybe.larva.entity.message;

import com.hybe.larva.entity.common.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "messages")
public class Message extends BaseEntity {

    private String title;

    private String content;

    private List<String> targets;

    @CreatedDate
    protected LocalDateTime pushingAt;

    @Builder
    public Message(String title, String content, List<String> targets) {
        this.title = title;
        this.content = content;
        this.targets = targets;
    }

}
