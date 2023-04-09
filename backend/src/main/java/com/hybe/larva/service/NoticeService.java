package com.hybe.larva.service;

import com.hybe.larva.dto.notice.*;
import com.hybe.larva.entity.locale_code.LocaleCodeRepoExt;
import com.hybe.larva.entity.notice.Notice;
import com.hybe.larva.entity.notice.NoticeRepoExt;
import com.hybe.larva.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepoExt noticeRepoExt;
//    private final LocaleCodeRepoExt localeCodeRepoExt;
    private final CacheUtil cacheUtil;

    public NoticeResponse addNotice(NoticeAddRequest request) {
        Notice notice = request.toEntity();
        notice = noticeRepoExt.insert(notice);
        return new NoticeResponse(notice);
    }

    public Page<NoticeResponse> searchNotice(NoticeSearchPageRequest request) {
        final Criteria criteria = Criteria.where(Notice.DELETED).ne(true);

        if (request.getFrom() != null && request.getTo() != null) {
            criteria.and(Notice.CREATED_AT).gte(request.getFrom()).lt(request.getTo());
        }
        if (request.getKeyword() != null) {
            criteria.and(Notice.TITLE).regex(request.getKeyword());
        }

        return noticeRepoExt.search(criteria, request.getPageable(), 0)
                .map(NoticeResponse::new);
    }

    public NoticeResponse getNotice(String noticeId) {
        Notice notice = noticeRepoExt.findById(noticeId);
        return new NoticeResponse(notice);
    }

    public NoticeResponse updateNotice(String noticeId, NoticeUpdateRequest request) {
        Notice notice = noticeRepoExt.findById(noticeId).update(request);
        notice = noticeRepoExt.save(notice);
        return new NoticeResponse(notice);
    }

    public void deleteNotice(String noticeId) {
        Notice notice = noticeRepoExt.findById(noticeId).delete();
        noticeRepoExt.save(notice);
    }

    public UserNoticeResponse getNoticeForUser(String noticeId) {
        Notice notice = noticeRepoExt.findById(noticeId);
        return new UserNoticeResponse(notice, cacheUtil);
    }

    public Page<UserNoticeResponse> searchNoticeForUser(NoticeSearchRequest request) {
        Criteria criteria = Criteria.where(Notice.DELETED).ne(true);
        LocalDateTime now = LocalDateTime.now();
        criteria.and(Notice.NOTICE_AT).lte(now);

        long count = noticeRepoExt.searchCount(criteria);
        if (count <= 0) {
            criteria = Criteria.where(Notice.DELETED).ne(true);
            criteria.and(Notice.NOTICE_AT).lte(now);
        }

        return noticeRepoExt.search(criteria, request.getPageable(), count)
                .map(n -> new UserNoticeResponse(n, cacheUtil));
    }


}
