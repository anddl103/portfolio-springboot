package com.hybe.larva.dto.user_info;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.user_info.AccountAgree;
import com.hybe.larva.entity.user_info.SignUpAccountAgree;
import com.hybe.larva.entity.user_info.UserInfo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString(callSuper = true)
@Getter
public class UserInfoResponse extends BaseResponse {

    private String uid;

    private Map<String, Boolean> deviceTokens;

    private SignUpAccountAgree signupAccountAgree;

    private AccountAgree withdrawal;

    @Setter
    private String email;

    @Setter
    private List<String> providers;

    public UserInfoResponse(UserInfo userInfo) {
        super(userInfo);

        this.uid = userInfo.getUid();
        this.deviceTokens = userInfo.getDeviceTokens();
        this.signupAccountAgree = userInfo.getSignupAccountAgree();
        this.withdrawal = userInfo.getWithdrawal();

    }
}
