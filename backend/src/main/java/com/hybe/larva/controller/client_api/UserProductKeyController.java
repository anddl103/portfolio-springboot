package com.hybe.larva.controller.client_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.product_key.*;
import com.hybe.larva.service.ProductKeyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.hybe.larva.consts.LarvaConst.SWAGGER_TAG_TOOL;
import static com.hybe.larva.enums.UserRole.ROLES.*;

@Api(tags = "ProductKey")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/productKeys")
@RestController
public class UserProductKeyController {

    private final ProductKeyService productKeyService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "제품 키 등록",
            notes = "제품 키를 사용자가 등록한다."
    )
    @Secured({USER})
    @PostMapping("/code/register")
    public SingleResult<ProductKeyTagResponse> registerProductKey(
            @RequestBody @Valid ProductKeyRegisterRequest request
    ) {
        ProductKeyTagResponse data = productKeyService.tagProductKey(request.getCode(), CommonUser.getUid());
        return ResponseHelper.newSingleSuccessResult(data);
    }
}
