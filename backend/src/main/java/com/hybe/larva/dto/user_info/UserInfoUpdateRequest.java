package com.hybe.larva.dto.user_info;

import com.hybe.larva.entity.user_info.AccountAgree;
import com.hybe.larva.entity.user_info.SignUpAccountAgree;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@ToString
@Getter
@Builder
public class UserInfoUpdateRequest {

    private Map<String, Boolean> deviceTokens;

    private SignUpAccountAgree signupAccountAgree;

    private AccountAgree withdrawal;
}
