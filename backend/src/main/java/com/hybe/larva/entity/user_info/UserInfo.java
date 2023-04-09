package com.hybe.larva.entity.user_info;

import com.hybe.larva.dto.user_info.UserInfoUpdateRequest;
import com.hybe.larva.entity.common.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "userInfo")
public class UserInfo extends BaseEntity  {

    public static final String USER_UID = "uid";
    public static final String DEVICE_TOKENS = "deviceTokens";

    @Indexed(unique = true)
    @NotNull
    private String uid;

    private boolean questionNewFlag;

    private Map<String, Boolean> deviceTokens;

    private SignUpAccountAgree signupAccountAgree;

    private AccountAgree withdrawal;

    @Builder
    public UserInfo(String uid, boolean questionNewFlag, Map<String, Boolean> deviceTokens,
                    SignUpAccountAgree signupAccountAgree, AccountAgree withdrawal) {
        this.uid = uid;
        this.questionNewFlag = questionNewFlag;
        this.deviceTokens = deviceTokens;
        this.signupAccountAgree = signupAccountAgree;
        this.withdrawal = withdrawal;
    }

    public UserInfo update(UserInfoUpdateRequest request) {
        Optional.ofNullable(request.getDeviceTokens()).ifPresent(v -> this.deviceTokens = v);
        Optional.ofNullable(request.getWithdrawal()).ifPresent(v -> this.withdrawal = v);
        Optional.ofNullable(request.getSignupAccountAgree()).ifPresent(v -> this.signupAccountAgree = v);
        return this;
    }

    public UserInfo updateQuestionNewFlag(boolean questionNewFlag) {
        Optional.ofNullable(questionNewFlag).ifPresent(v -> this.questionNewFlag = v);
        return this;
    }

    public UserInfo updateTermsOfService(boolean termsOfService) {
        Optional.ofNullable(termsOfService).ifPresent(v -> {
            this.signupAccountAgree.getTermsOfService().setFlag(v);
            if (termsOfService == true) {
                this.signupAccountAgree.getTermsOfService().setUpdatedAt(LocalDateTime.now());
            }
        });
        return this;
    }

    public UserInfo updateOver14YearsOld(boolean over14YearsOld) {
        Optional.ofNullable(over14YearsOld).ifPresent(v -> {
            this.signupAccountAgree.getOver14YearsOld().setFlag(v);
            if (over14YearsOld == true) {
                this.signupAccountAgree.getOver14YearsOld().setUpdatedAt(LocalDateTime.now());
            }
        });
        return this;
    }

    public UserInfo updatePrivacyPolicy(boolean privacyPolicy) {
        Optional.ofNullable(privacyPolicy).ifPresent(v -> {
            this.signupAccountAgree.getPrivacyPolicy().setFlag(v);
            if (privacyPolicy == true) {
                this.signupAccountAgree.getPrivacyPolicy().setUpdatedAt(LocalDateTime.now());
            }
        });
        return this;
    }


    public UserInfo updatePush(boolean push) {
        Optional.ofNullable(push).ifPresent(v -> {
            this.signupAccountAgree.getPush().setFlag(v);
            this.signupAccountAgree.getPush().setUpdatedAt(LocalDateTime.now());
        });
        return this;
    }

    public UserInfo updateWithdrawal(boolean withdrawal) {
        Optional.ofNullable(withdrawal).ifPresent(v -> {
            this.withdrawal.setFlag(v);
            this.withdrawal.setUpdatedAt(LocalDateTime.now());
        });
        return this;
    }

    public UserInfo delete() {
        this.deleted = true;
        return this;
    }
}
