package com.hybe.larva.entity.policy;

import com.hybe.larva.dto.policy.PolicyUpdateRequest;
import com.hybe.larva.entity.common.BaseEntity;
import com.hybe.larva.entity.common.LocaleCodeContents;
import com.hybe.larva.enums.Usage;
import lombok.*;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Optional;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "policy")
public class Policy extends BaseEntity {

    public static final String USAGE = "usage";
    public static final String VERSION = "version";

    private Usage usage;

    private Map<String, LocaleCodeContents> localeCodeContents;

    private int version;

    @Builder
    public Policy(Usage usage, Map<String, LocaleCodeContents> localeCodeContents, int version) {
        this.usage = usage;
        this.localeCodeContents = localeCodeContents;
        this.version = version;
    }

    public Policy update(PolicyUpdateRequest request) {
        Optional.ofNullable(request.getUsage()).ifPresent(v -> this.usage = v);
        Optional.ofNullable(request.getLocaleCodeContents()).ifPresent(v -> this.localeCodeContents = v);

        return this;
    }

    public Policy delete() {
        this.deleted = true;
        return this;
    }
}
