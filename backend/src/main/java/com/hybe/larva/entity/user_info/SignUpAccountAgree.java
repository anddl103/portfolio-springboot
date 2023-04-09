package com.hybe.larva.entity.user_info;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class SignUpAccountAgree {

    private AccountAgree termsOfService;

    private AccountAgree privacyPolicy;

    private AccountAgree over14YearsOld;

    private AccountAgree push;
}
