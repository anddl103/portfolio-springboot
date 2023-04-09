package com.hybe.larva.dto.notice;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.common.LocaleCodeContents;
import com.hybe.larva.entity.notice.Notice;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

@ToString(callSuper = true)
@Getter
public class NoticeResponse extends BaseResponse {

    private Map<String, LocaleCodeContents> localeCodeContents;

    private LocalDateTime noticeAt;

    public NoticeResponse(Notice notice) {
        super(notice);

        this.localeCodeContents = notice.getLocaleCodeContents();
        this.noticeAt = notice.getNoticeAt();
    }

}
