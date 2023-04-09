package com.hybe.larva.entity.notice;

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
public class NoticeRepoExt {

    private final NoticeRepo repo;
    private final MongoOperations mongo;

    public Notice insert(Notice notice) {
        return repo.insert(notice);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public void delete(Notice notice) {
        repo.delete(notice);
    }

    public Notice save(Notice notice) {
        return repo.save(notice);
    }

    public Notice findById(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find Notice: id=" + id));
    }

    public Page<Notice> search(Criteria criteria, Pageable pageable, long count) {
        Query query = new Query(criteria).with(pageable);
        List<Notice> notices = mongo.find(query, Notice.class);
        return PageableExecutionUtils.getPage(notices, pageable,
                () -> (count > 0 ? count : searchCount(criteria))
        );
    }

    public long searchCount(Criteria criteria) {
        return mongo.count(new Query(criteria), Notice.class);
    }
}
