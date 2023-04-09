package com.hybe.larva.entity.question;

import com.hybe.larva.dto.common.CommonUser;
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
public class QuestionRepoExt {

    private final QuestionRepo repo;
    private final MongoOperations mongo;

    public Question insert(Question question) {
        return repo.insert(question);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public void delete(Question question) {
        repo.delete(question);
    }

    public Question save(Question question) {
        return repo.save(question);
    }

    public Question findById(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find Question: id=" + id));
    }

    public Page<Question> search(Criteria criteria, Pageable pageable) {
        Query query = new Query(criteria).with(pageable);
        List<Question> questions = mongo.find(query, Question.class);
        return PageableExecutionUtils.getPage(questions, pageable,
                () -> mongo.count(new Query(criteria), Question.class)
        );
    }

    public Question findByIdAndCreatedBy(String id, String uid) {
        return repo.findByIdAndCreatedBy(id, uid).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find Question: id=" + id));
    }
}
