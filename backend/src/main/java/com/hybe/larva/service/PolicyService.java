package com.hybe.larva.service;

import com.hybe.larva.dto.policy.*;
import com.hybe.larva.entity.policy.Policy;
import com.hybe.larva.entity.policy.PolicyRepoExt;
import com.hybe.larva.enums.Usage;
import com.hybe.larva.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PolicyService {

    private final PolicyRepoExt policyRepoExt;
    private final CacheUtil cacheUtil;

    public PolicyResponse addPolicy(PolicyAddRequest request) {
        Policy policy = request.toEntity(getVersion(request.getUsage()));
        policy = policyRepoExt.insert(policy);
        return new PolicyResponse(policy);
    }

    public Page<PolicyResponse> searchPolicy(PolicySearchRequest request) {
        final Criteria criteria = Criteria.where(Policy.DELETED).ne(true);

        if (request.getFrom() != null && request.getTo() != null) {
            criteria.and(Policy.CREATED_AT).gte(request.getFrom()).lt(request.getTo());
        }

        return policyRepoExt.search(criteria, request.getPageable())
                .map(PolicyResponse::new);
    }

    public PolicyResponse getPolicy(String cardId) {
        Policy policy = policyRepoExt.findById(cardId);
        return new PolicyResponse(policy);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public PolicyResponse updatePolicy(String policyId, PolicyUpdateRequest request) {
        Policy policy = policyRepoExt.findById(policyId).update(request);
        policy = policyRepoExt.save(policy);
        return new PolicyResponse(policy);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public void deletePolicy(String policyId) {
        Policy policy = policyRepoExt.findById(policyId).delete();
        policyRepoExt.save(policy);
    }

    public UserPolicyResponse getPolicyUsage(Usage usage, String lang) {
        Policy policy = policyRepoExt.findByUsage(usage);
        return new UserPolicyResponse(policy, cacheUtil, lang);
    }

    public int getVersion(Usage usage) {
        return policyRepoExt.getVersion(usage);
    }
}
