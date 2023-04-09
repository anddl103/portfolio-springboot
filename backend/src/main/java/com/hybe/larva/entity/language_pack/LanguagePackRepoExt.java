package com.hybe.larva.entity.language_pack;


import com.hybe.larva.entity.nfc_card.NfcCard;
import com.hybe.larva.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class LanguagePackRepoExt {

    private final LanguagePackRepo repo;
    private final MongoOperations mongo;

    public LanguagePack insert(LanguagePack languagePack) {
        return repo.insert(languagePack);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public void delete(LanguagePack languagePack) {
        repo.delete(languagePack);
    }

    public LanguagePack save(LanguagePack languagePack) {
        return repo.save(languagePack);
    }

    public LanguagePack findById(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find LanguagePack: id=" + id));
    }

    public Page<LanguagePack> search(Criteria criteria, Pageable pageable) {
        Query query = new Query(criteria).with(pageable);
        List<LanguagePack> languagePacks = mongo.find(query, LanguagePack.class);
        return PageableExecutionUtils.getPage(languagePacks, pageable,
                () -> mongo.count(new Query(criteria), LanguagePack.class)
        );
    }

    public List<LanguagePack> findAllLanguagePack() {
        final Criteria criteria = Criteria.where(NfcCard.DELETED).ne(true);
        Query query = new Query(criteria);
        query.with(Sort.by(Sort.Direction.DESC, LanguagePack.CREATED_AT));
        List<LanguagePack> languagePacks = mongo.find(query, LanguagePack.class);

        return languagePacks;
    }

}
