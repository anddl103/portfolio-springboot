package com.hybe.larva.dto.policy;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.common.LocaleCodeContents;
import com.hybe.larva.entity.policy.Policy;
import com.hybe.larva.enums.Usage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@ToString(callSuper = true)
@Getter
public class PolicyResponse extends BaseResponse {

    private Usage usage;

    private Map<String, LocaleCodeContents> localeCodeContents;

    private final int version;

    public PolicyResponse(Policy policy) {
        super(policy);

        this.usage = policy.getUsage();
        this.localeCodeContents = policy.getLocaleCodeContents();
        this.version = policy.getVersion();
    }
}
