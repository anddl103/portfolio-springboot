package com.hybe.larva.entity.policy;

import com.hybe.larva.enums.Usage;
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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class PolicyRepoExt {
    
    private final PolicyRepo repo;
    private final MongoOperations mongo;

    public Policy insert(Policy policy) {
        return repo.insert(policy);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public void delete(Policy policy) {
        repo.delete(policy);
    }

    public Policy save(Policy policy) {
        return repo.save(policy);
    }

    public Policy findById(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find policy: id=" + id));
    }

    public Page<Policy> search(Criteria criteria, Pageable pageable) {
        Query query = new Query(criteria).with(pageable);
        List<Policy> policys = mongo.find(query, Policy.class);
        return PageableExecutionUtils.getPage(policys, pageable,
                () -> mongo.count(new Query(criteria), Policy.class)
        );
    }

    public int getVersion(Usage usage) {
        Criteria criteria = Criteria.where(Policy.DELETED).ne(true);
        criteria.and(Policy.USAGE).is(usage);
        Query query = new Query(criteria).with(Sort.by(Sort.Direction.DESC, Policy.VERSION)).limit(1);
        return Optional.ofNullable(mongo.findOne(query, Policy.class)).map(Policy::getVersion).orElse(0)+1;
    }

    public Policy findByUsage(Usage usage) {
        Criteria criteria = Criteria.where(Policy.DELETED).ne(true);
        criteria.and(Policy.USAGE).is(usage);
        Query query = new Query(criteria).with(Sort.by(Sort.Direction.DESC, Policy.VERSION)).limit(1);
        return Optional.ofNullable(mongo.findOne(query, Policy.class)).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find policy: usage=" + usage));
    }

}
