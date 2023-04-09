package com.hybe.larva.dto.notice;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.common.LocaleCodeContents;
import com.hybe.larva.entity.notice.Notice;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

@ToString(callSuper = true)
@Getter
public class UserNoticeResponse extends BaseResponse {

    private String subject;

    private String contents;

    private LocalDateTime noticeAt;

    public UserNoticeResponse(Notice notice, CacheUtil cacheUtil) {
        super(notice);

        this.subject = notice.getLocaleCodeContents().get(cacheUtil.getLanguage()).getSubject();
        this.contents = notice.getLocaleCodeContents().get(cacheUtil.getLanguage()).getContents();

        this.noticeAt = notice.getNoticeAt();
    }

}
