package com.hybe.larva.dto.policy;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.common.LocaleCodeContents;
import com.hybe.larva.entity.policy.Policy;
import com.hybe.larva.enums.Usage;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@ToString(callSuper = true)
@Getter
public class UserPolicyResponse extends BaseResponse {

    private Usage usage;

    private String subject;

    private String contents;

    private final int version;

    public UserPolicyResponse(Policy policy, CacheUtil cacheUtil, String lang) {
        super(policy);

        this.usage = policy.getUsage();
        this.subject = policy.getLocaleCodeContents().get(cacheUtil.getLanguage(lang)).getSubject();
        this.contents = policy.getLocaleCodeContents().get(cacheUtil.getLanguage(lang)).getContents();
        this.version = policy.getVersion();
    }
}
