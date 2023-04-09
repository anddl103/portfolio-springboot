package com.hybe.larva.entity.faq_category;

import com.hybe.larva.entity.faq.Faq;
import com.hybe.larva.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class FaqCategoryRepoExt {

    private final FaqCategoryRepo repo;
    private final MongoOperations mongo;

    public FaqCategory insert(FaqCategory faqCategory) {
        return repo.insert(faqCategory);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public void delete(FaqCategory faqCategory) {
        repo.delete(faqCategory);
    }

    public FaqCategory save(FaqCategory faqCategory) {
        return repo.save(faqCategory);
    }

    public FaqCategory findById(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find Faq: id=" + id));
    }

    public Page<FaqCategory> search(Criteria criteria, Pageable pageable) {
        Query query = new Query(criteria).with(pageable);
        List<FaqCategory> faqCategories = mongo.find(query, FaqCategory.class);
        return PageableExecutionUtils.getPage(faqCategories, pageable,
                () -> mongo.count(new Query(criteria), FaqCategory.class)
        );
    }
}
