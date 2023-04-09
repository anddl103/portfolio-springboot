package com.hybe.larva.dto.user_info;

import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.entity.user_info.AccountAgree;
import com.hybe.larva.entity.user_info.SignUpAccountAgree;
import com.hybe.larva.entity.user_info.UserInfo;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

@ToString
@Data
@Builder
public class UserInfoAddRequest {

    private Map<String, Boolean> deviceTokens;

    private boolean push;

    public UserInfo toEntity(String uid) {
        return UserInfo.builder()
                .uid(uid)
                .questionNewFlag(false)
                .deviceTokens(deviceTokens)
                .signupAccountAgree(SignUpAccountAgree.builder()
                    .over14YearsOld(AccountAgree.builder().flag(true).updatedAt(LocalDateTime.now()).build())
                    .termsOfService(AccountAgree.builder().flag(true).updatedAt(LocalDateTime.now()).build())
                    .privacyPolicy(AccountAgree.builder().flag(true).updatedAt(LocalDateTime.now()).build())
                    .push(AccountAgree.builder().flag(push).updatedAt(LocalDateTime.now()).build())
                .build())
                .withdrawal(AccountAgree.builder().flag(false).updatedAt(LocalDateTime.now()).build())
                .build();
    }
}
