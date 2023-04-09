package com.hybe.larva.controller.client_api;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.user_info.UserInfoAddRequest;
import com.hybe.larva.dto.user_info.UserInfoResponse;
import com.hybe.larva.dto.user_info.UserInfoUpdateRequest;
import com.hybe.larva.exception.DeletedResourceException;
import com.hybe.larva.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.hybe.larva.enums.UserRole.ROLES.*;

@Slf4j
@Api(tags = "Me")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/users/me")
@RestController
public class MeController {

    private final UserInfoService userInfoService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "유저 정보 추가",
            notes = "유저 정보를 추가한다."
    )
    @Secured({USER})
    @PostMapping("")
    public SingleResult<UserInfoResponse> addUserInfo(
            @RequestBody @Valid UserInfoAddRequest request
    )  {
        UserInfoResponse userInfoResponse =
                userInfoService.addUserInfo(request);

        return ResponseHelper.newSingleSuccessResult(userInfoResponse);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "유저 정보 조회",
            notes = "유저 정보를 조회한다."
    )
    @Secured({USER})
    @GetMapping("")
    public SingleResult<UserInfoResponse> getUserInfo(
    ) {
        UserInfoResponse data = userInfoService.getUserInfoUid(CommonUser.getUid());
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "유저 정보 수정",
            notes = "유저 정보를 수정한다."
    )
    @Secured({USER})
    @PatchMapping("")
    public SingleResult<UserInfoResponse> updateUserInfo(
            @ApiParam(value = "업데이트 정보")
            @RequestBody @Valid UserInfoUpdateRequest request
    ) {
        UserInfoResponse data = userInfoService.updateUserInfoUid(CommonUser.getUid(), request);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "푸쉬 알림 동의 여부 수정",
            notes = "푸쉬 알림 동의 여부를 수정한다."
    )
    @Secured({USER})
    @PatchMapping("push")
    public SingleResult<UserInfoResponse> updateUserInfoPush(
            @ApiParam(value = "push")
            @RequestParam(required = false, defaultValue = "true") boolean push
    ) {
        UserInfoResponse data = userInfoService.updateUserInfoPushUid(CommonUser.getUid(), push);
        return ResponseHelper.newSingleSuccessResult(data);
    }

//    @ApiOperation(
//            tags = {LarvaConst.SWAGGER_TAG_USER},
//            value = "만 14세 이상 동의 여부 수정",
//            notes = "만 14세 이상 동의 여부를 수정한다."
//    )
////    @Secured({ArtistRole.FULL_DIRECTOR})
//    @PatchMapping("over14")
//    public SingleResult<UserInfoResponse> updateUserInfoOver14YearsOld(
//            @ApiParam(value = "over14")
//            @RequestParam(required = false, defaultValue = "true") boolean push
//    ) {
//        UserInfoResponse data = userInfoService.updateUserInfoOver14YearsOld(CommonUser.getUid(), push);
//        return ResponseHelper.newSingleSuccessResult(data);
//    }
//
//    @ApiOperation(
//            tags = {LarvaConst.SWAGGER_TAG_USER},
//            value = "개인 정보 수집 동의 여부 수정",
//            notes = "개인 정보 수집 동의 여부를 수정한다."
//    )
////    @Secured({ArtistRole.FULL_DIRECTOR})
//    @PatchMapping("privacy")
//    public SingleResult<UserInfoResponse> updateUserInfoPrivacyPolicy(
//            @ApiParam(value = "privacy")
//            @RequestParam(required = false, defaultValue = "true") boolean push
//    ) {
//        UserInfoResponse data = userInfoService.updateUserInfoPrivacyPolicy(CommonUser.getUid(), push);
//        return ResponseHelper.newSingleSuccessResult(data);
//    }
//
//    @ApiOperation(
//            tags = {LarvaConst.SWAGGER_TAG_USER},
//            value = "이용 약관 동의 여부 수정",
//            notes = "이용 약관 동의 여부를 수정한다."
//    )
////    @Secured({ArtistRole.FULL_DIRECTOR})
//    @PatchMapping("terms")
//    public SingleResult<UserInfoResponse> updateUserInfoTermsOfService(
//            @ApiParam(value = "terms")
//            @RequestParam(required = false, defaultValue = "true") boolean push
//    ) {
//        UserInfoResponse data = userInfoService.updateUserInfoTermsOfService(CommonUser.getUid(), push);
//        return ResponseHelper.newSingleSuccessResult(data);
//    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "토큰 정보 수정",
            notes = "토큰 정보를 수정 한다."
    )
    @Secured({USER})
    @PatchMapping("deviceToken")
    public SingleResult<UserInfoResponse> updateResignation(
            @ApiParam(value = "deviceToken")
            @RequestParam(required = false) String deviceToken
    ) {
        UserInfoResponse data = userInfoService.updateDeviceTokensUid(deviceToken);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "계정 탈퇴",
            notes = "계정을 탈퇴 한다."
    )
    @Secured({USER})
    @PatchMapping("withdrawal")
    public SingleResult<UserInfoResponse> membershipResignation(
            @ApiParam(value = "withdrawal")
            @RequestParam(required = false, defaultValue = "true") boolean withdrawal
    ) {
        String uid = CommonUser.getUid();
        UserInfoResponse data = userInfoService.updateUserInfoWithdrawalUid(uid, withdrawal);
        if (Boolean.TRUE.equals(withdrawal)) {
            try {
                FirebaseAuth.getInstance().deleteUser(uid);
            } catch (FirebaseAuthException e) {
                throw new DeletedResourceException(" Failed to delete user");
            }
        }

        return ResponseHelper.newSingleSuccessResult(data);
    }
}
