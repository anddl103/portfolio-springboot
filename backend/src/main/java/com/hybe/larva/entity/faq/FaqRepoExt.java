package com.hybe.larva.entity.faq;

import com.hybe.larva.enums.Category;
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
public class FaqRepoExt {

    private final FaqRepo repo;
    private final MongoOperations mongo;

    public Faq insert(Faq category) {
        return repo.insert(category);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public void delete(Faq category) {
        repo.delete(category);
    }

    public Faq save(Faq category) {
        return repo.save(category);
    }

    public Faq findById(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find Faq: id=" + id));
    }

    public Page<Faq> search(Criteria criteria, Pageable pageable) {
        Query query = new Query(criteria).with(pageable);
        List<Faq> faqs = mongo.find(query, Faq.class);
        return PageableExecutionUtils.getPage(faqs, pageable,
                () -> mongo.count(new Query(criteria), Faq.class)
        );
    }

//    public List<Faq> findAllByCategoryAndLocaleCode(Category category, String localeCode) {
//        return repo.findAllByCategoryAndLocaleCode(category, localeCode);
//    }



}
