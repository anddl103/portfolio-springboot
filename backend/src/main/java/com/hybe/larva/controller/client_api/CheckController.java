package com.hybe.larva.controller.client_api;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.EmailDuplicationCheckResponse;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.user_info.UserInfoResponse;
import com.hybe.larva.entity.user_info.UserInfo;
import com.hybe.larva.exception.ResourceNotFoundException;
import com.hybe.larva.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Me")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/check")
@RestController
public class CheckController {

    private final UserInfoService userInfoService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "이메일 중복 검사",
            notes = "이메일이 중복되는지 검사한다."
    )
    @GetMapping("/duplicate/{email}")
    public SingleResult<EmailDuplicationCheckResponse> checkUserEmail(
            @ApiParam(value = "이메일")
            @PathVariable String email
    )  {
        boolean duplicated = false;
        boolean disabled = false;
        try {
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
            if (userRecord != null) {

                try {
                    UserInfoResponse userInfo = userInfoService.getUserInfoUid(userRecord.getUid());
                    if (userInfo != null) {
                        duplicated = true;
                    }

                } catch (ResourceNotFoundException e) {

                }

                disabled = userRecord.isDisabled();
            }
//            userRecord.isDisabled()
        } catch (FirebaseAuthException e) {

        }
        EmailDuplicationCheckResponse data = new EmailDuplicationCheckResponse(duplicated, disabled);
        return ResponseHelper.newSingleSuccessResult(data);
    }
}
