package com.hybe.larva.entity.locale_code;

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
public class LocaleCodeRepoExt {

    private final LocaleCodeRepo repo;
    private final MongoOperations mongo;

    public LocaleCodes insert(LocaleCodes localeCodes) {
        return repo.insert(localeCodes);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public void delete(LocaleCodes localeCodes) {
        repo.delete(localeCodes);
    }

    public LocaleCodes save(LocaleCodes localeCodes) {
        return repo.save(localeCodes);
    }

    public LocaleCodes findById(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find artist: id=" + id));
    }

    public List<LocaleCodes> findAll() {
        final Criteria criteria = Criteria.where(LocaleCodes.DELETED).ne(true);
        Query query = new Query(criteria);
        return mongo.find(query, LocaleCodes.class);
    }

    public Page<LocaleCodes> search(Criteria criteria, Pageable pageable) {
        Query query = new Query(criteria).with(pageable);
        List<LocaleCodes> localeCodes = mongo.find(query, LocaleCodes.class);
        return PageableExecutionUtils.getPage(localeCodes, pageable,
                () -> mongo.count(new Query(criteria), LocaleCodes.class)
        );
    }
}
