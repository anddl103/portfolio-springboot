package com.hybe.larva.service;

import com.hybe.larva.dto.faq.*;
import com.hybe.larva.entity.faq.Faq;
import com.hybe.larva.entity.faq.FaqRepoExt;
import com.hybe.larva.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class FaqService {

    private final FaqRepoExt faqRepoExt;
    private final CacheUtil cacheUtil;

    public FaqResponse addFaq(FaqAddRequest request) {
        Faq faq = request.toEntity();
        faq = faqRepoExt.insert(faq);
        return new FaqResponse(faq);
    }

    public FaqResponse getFaq(String faqId) {
        Faq faq = faqRepoExt.findById(faqId);
        return new FaqResponse(faq);
    }

    public FaqResponse updateFaq(String faqId, FaqUpdateRequest request) {
        Faq faq = faqRepoExt.findById(faqId).update(request);
        faq = faqRepoExt.save(faq);
        return new FaqResponse(faq);
    }

    public void deleteFaq(String faqId) {
        Faq faq = faqRepoExt.findById(faqId).delete();
        faqRepoExt.save(faq);
    }

    public Page<FaqResponse> searchFaq(FaqSearchPageRequest request) {
        final Criteria criteria = Criteria.where(Faq.DELETED).ne(true);

        if (request.getFrom() != null && request.getTo() != null) {
            criteria.and(Faq.CREATED_AT).gte(request.getFrom()).lt(request.getTo());
        }

        if (request.getKeyword() != null) {
            criteria.and(Faq.TITLE).regex(request.getKeyword());
        }

        if (request.getCategory() != null) {
            criteria.and(Faq.CATEGORY).is(request.getCategory());
        }

        return faqRepoExt.search(criteria, request.getPageable())
                .map(FaqResponse::new);
    }

    public UserFaqResponse getFaqForUser(String faqId, String lang) {
        Faq faq = faqRepoExt.findById(faqId);
        return new UserFaqResponse(faq, cacheUtil, lang);
    }

    public Page<UserFaqResponse> getFaqCategoryForUser(String category, FaqSearchRequest request, String lang) {
        final Criteria criteria = Criteria.where(Faq.DELETED).ne(true);

        criteria.and(Faq.CATEGORY).is(category);

        if (lang != null && !lang.equals("") && !lang.trim().equals("")) {
            criteria.and(Faq.LOCALE_CODE_CONTENTS+"."+lang).ne(new HashMap<>());
        }

        return faqRepoExt.search(criteria, request.getPageable())
                .map(u -> new UserFaqResponse(u, cacheUtil, lang));
    }
}
