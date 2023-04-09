package com.hybe.larva.entity.notice;

import com.hybe.larva.dto.notice.NoticeUpdateRequest;
import com.hybe.larva.entity.common.BaseEntity;
import com.hybe.larva.entity.common.LocaleCodeContents;
import lombok.*;
import org.checkerframework.checker.formatter.qual.Format;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "notices")
public class Notice extends BaseEntity {
    public static final String TITLE = "title";
    public static final String LOCALE_CODE = "localeCode";
    public static final String NOTICE_AT = "noticeAt";


    private Map<String, LocaleCodeContents> localeCodeContents;

    private LocalDateTime noticeAt;

    @Builder
    public Notice (Map<String, LocaleCodeContents> localeCodeContents, LocalDateTime noticeAt) {
        this.localeCodeContents = localeCodeContents;
        this.noticeAt = noticeAt;
    }


    public Notice update(NoticeUpdateRequest request) {

        Optional.ofNullable(request.getLocaleCodeContents()).ifPresent(v -> this.localeCodeContents = v);
        Optional.ofNullable(request.getNoticeAt()).ifPresent(v -> this.noticeAt = v);

        return this;
    }

    public Notice delete() {
        this.deleted = true;
        return this;
    }
}
