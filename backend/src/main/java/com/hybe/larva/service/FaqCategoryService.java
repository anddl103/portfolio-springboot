package com.hybe.larva.service;

import com.hybe.larva.dto.faq.FaqAddRequest;
import com.hybe.larva.dto.faq.FaqSearchPageRequest;
import com.hybe.larva.dto.faq.FaqUpdateRequest;
import com.hybe.larva.dto.faq_category.*;
import com.hybe.larva.entity.faq.Faq;
import com.hybe.larva.entity.faq_category.FaqCategory;
import com.hybe.larva.entity.faq_category.FaqCategoryRepoExt;
import com.hybe.larva.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FaqCategoryService {

    private final FaqCategoryRepoExt faqCategoryRepoExt;
    private final CacheUtil cacheUtil;

    public FaqCategoryResponse addFaqCategory(FaqCategoryAddRequest request) {
        FaqCategory faqCategory = request.toEntity();
        faqCategory = faqCategoryRepoExt.insert(faqCategory);
        return new FaqCategoryResponse(faqCategory);
    }

    public FaqCategoryResponse getFaqCategory(String faqId) {
        FaqCategory faqCategory = faqCategoryRepoExt.findById(faqId);
        return new FaqCategoryResponse(faqCategory);
    }

    public FaqCategoryResponse updateFaqCategory(String faqId, FaqCategoryUpdateRequest request) {
        FaqCategory faqCategory = faqCategoryRepoExt.findById(faqId).update(request);
        faqCategory = faqCategoryRepoExt.save(faqCategory);
        return new FaqCategoryResponse(faqCategory);
    }

    public void deleteFaqCategory(String faqId) {
        FaqCategory faqCategory = faqCategoryRepoExt.findById(faqId).delete();
        faqCategoryRepoExt.save(faqCategory);
    }

    public Page<FaqCategoryResponse> searchFaqCategory(FaqCategorySearchRequest request) {
        final Criteria criteria = Criteria.where(Faq.DELETED).ne(true);

        if (request.getFrom() != null && request.getTo() != null) {
            criteria.and(FaqCategory.CREATED_AT).gte(request.getFrom()).lt(request.getTo());
        }

        if (request.getKeyword() != null) {
            criteria.and(FaqCategory.COMMENT).regex(request.getKeyword());
        }

        return faqCategoryRepoExt.search(criteria, request.getPageable())
                .map(FaqCategoryResponse::new);
    }

//    public UserFaqCategoryResponse getFaqForUser(String faqId) {
//        Faq faq = faqCategoryRepoExt.findById(faqId);
//        return new UserFaqCategoryResponse(faq);
//    }
//
//    public Page<UserFaqCategoryResponse> getFaqCategoryForUser(String category, FaqSearchRequest request) {
//        final Criteria criteria = Criteria.where(Faq.DELETED).ne(true);
//
//        criteria.and(Faq.CATEGORY).is(category);
//
//        return faqCategoryRepoExt.search(criteria, request.getPageable())
//                .map(UserFaqCategoryResponse::new);
//    }

    public Page<UserFaqCategoryResponse> searchFaqCategoryForUser(FaqCategorySearchRequest request, String lang) {
        final Criteria criteria = Criteria.where(FaqCategory.DELETED).ne(true);

        return faqCategoryRepoExt.search(criteria, request.getPageable())
                .map(c -> new UserFaqCategoryResponse(c, cacheUtil, lang));
    }

    public UserFaqCategoryResponse getFaqCategoryForUser(String faqCategoryId, String lang) {
        FaqCategory faqCategory = faqCategoryRepoExt.findById(faqCategoryId);
        return new UserFaqCategoryResponse(faqCategory, cacheUtil, lang);
    }
}
