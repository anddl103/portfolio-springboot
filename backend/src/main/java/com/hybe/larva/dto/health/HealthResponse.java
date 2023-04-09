package com.hybe.larva.dto.health;

import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.util.CacheUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@ToString
@Getter

public class HealthResponse {

    // status: OK
    @NotBlank
    private final String status;

    // profile: [dev|test|live]
    @NotBlank
    private final String profile;

    // version
    @NotBlank
    private final String version;

    // cache sample
    public HealthResponse (String key, CacheUtil cacheUtil) {
        this.status = "OK";
        this.profile = cacheUtil.getLanguageResponse(key);
        this.version = CommonUser.getUid();
    }


    @Builder
    public HealthResponse(String status, String profile, String version) {
        this.status = status;
        this.profile = profile;
        this.version = version;
    }

}
