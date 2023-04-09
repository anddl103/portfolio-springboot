package com.hybe.larva.controller.client_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.policy.*;
import com.hybe.larva.enums.Usage;
import com.hybe.larva.service.PolicyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static com.hybe.larva.enums.UserRole.ROLES.*;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Api(tags = "Policy")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/user/policies")
@RestController
public class UserPolicyController {

    private final PolicyService policyService;


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "이용 약관 내용 조회",
            notes = "이용 약관 내용을 조회한다."
    )

    @GetMapping("/termsOfService")
    public SingleResult<UserPolicyResponse> getPolicyTermsOfService() {
        UserPolicyResponse data = policyService.getPolicyUsage(Usage.TERMS_OF_SERVICE, null);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "서비스 운영 정책 내용 조회",
            notes = "서비스 운영 정책 내용을 조회한다."
    )
    @GetMapping("/serviceOperationPolicy")
    public SingleResult<UserPolicyResponse> getPolicyServiceOperationPolicy() {
        UserPolicyResponse data = policyService.getPolicyUsage(Usage.SERVICE_OPERATION_POLICY, null);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "개인 정보 처리 내용 조회",
            notes = "개인 정보 처리 내용을 조회한다."
    )
    @GetMapping("/privacyPolicy")
    public SingleResult<UserPolicyResponse> getPolicyPrivacyPolicy() {
        UserPolicyResponse data = policyService.getPolicyUsage(Usage.PRIVACY_POLICY, null);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "개인 정보 처리 내용 조회",
            notes = "개인 정보 처리 내용을 조회한다."
    )
    @GetMapping("/{policy}")
    public SingleResult<UserPolicyResponse> getPolicyUsage(
        @ApiParam(value = "이용 약관 정책")
        @PathVariable String policy
    ) {
        UserPolicyResponse data = policyService.getPolicyUsage(Usage.valueOf(policy), null);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "푸시 내용 조회",
            notes = "푸시 내용을 조회한다."
    )
    @GetMapping("/push")
    public SingleResult<UserPolicyResponse> getPolicyPush() {
        UserPolicyResponse data = policyService.getPolicyUsage(Usage.PUSH, null);
        return ResponseHelper.newSingleSuccessResult(data);
    }
}
