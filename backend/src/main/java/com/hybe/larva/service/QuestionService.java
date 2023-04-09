package com.hybe.larva.service;

import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.dto.question.*;
import com.hybe.larva.entity.question.Question;
import com.hybe.larva.entity.question.QuestionRepoExt;
import com.hybe.larva.entity.user_info.UserInfo;
import com.hybe.larva.entity.user_info.UserInfoRepoExt;
import com.hybe.larva.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepoExt questionRepoExt;
    private final UserInfoRepoExt userInfoRepoExt;
    private final CacheUtil cacheUtil;



    public QuestionResponse getQuestion(String questionId) {
        Question question = questionRepoExt.findById(questionId);
        return new QuestionResponse(question);
    }

    public QuestionResponse updateQuestion(String questionId, QuestionUpdateRequest request) {
        Question question = questionRepoExt.findById(questionId).update(request);
        question = questionRepoExt.save(question);

        // 사용자 뱃지 활성화
        UserInfo userInfo = userInfoRepoExt.findByUid(question.getCreatedBy()).updateQuestionNewFlag(true);
        userInfoRepoExt.save(userInfo);
        return new QuestionResponse(question);
    }

    public void deleteQuestion(String questionId) {
        Question question = questionRepoExt.findById(questionId).delete();
        questionRepoExt.save(question);
    }

    public Page<QuestionResponse> searchQuestion(QuestionSearchPageRequest request) {
        final Criteria criteria = Criteria.where(Question.DELETED).ne(true);

        if (request.getFrom() != null && request.getTo() != null) {
            criteria.and(Question.CREATED_AT).gte(request.getFrom()).lt(request.getTo());
        }

        return questionRepoExt.search(criteria, request.getPageable())
                .map(QuestionResponse::new);
    }

    public UserQuestionResponse addQuestionForUser(QuestionAddRequest request) {
        Question question = request.toEntity(cacheUtil);
        question = questionRepoExt.insert(question);
        return new UserQuestionResponse(question);
    }


    public Page<UserQuestionResponse> searchQuestionForUser(QuestionSearchRequest request) {

        final Criteria criteria = Criteria.where(Question.DELETED).ne(true);

        criteria.and(Question.CREATED_BY).is(CommonUser.getUid());

        return questionRepoExt.search(criteria, request.getPageable())
                .map(UserQuestionResponse::new);
    }

    public UserQuestionResponse getQuestionForUser(String questionId, String uid) {

        Question question = questionRepoExt.findByIdAndCreatedBy(questionId, uid);
        return new UserQuestionResponse(question);
    }
}
