package com.hybe.larva.dto.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@Builder
public class AdminResponse {

    private String uid;

    private String email;

    private String role;

    private long createdAt;

    private long lastSignInTime;
}
