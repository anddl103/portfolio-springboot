package com.hybe.larva.controller.backoffice_api;

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
public class ProductKeyController {

    private final ProductKeyService productKeyService;

    // 제품 키 생성 후 NFC 카드에 쓰기
    // 제품 키 검증 필요
    // 사용자가 nfc 태깅 처리 필요
    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_TOOL},
            value = "제품 키 생성",
            notes = "제품 키를 추가한다."
    )
    @Secured({PRODUCT_MANAGER})
    @PostMapping("")
    public SingleResult<ProductKeyResponse> addProductKey(
            @RequestBody @Valid ProductKeyAddRequest request
    ) {
        ProductKeyResponse data = productKeyService.addProductKey(request);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {SWAGGER_TAG_TOOL},
            value = "제품 키 검증",
            notes = "제품 키 가 정상적으로 발급되었는지 검증한다."
    )
    @Secured({PRODUCT_MANAGER})
    @PostMapping("/code/verify")
    public SingleResult<ProductKeyResponse> verifyProductKey(
            @RequestBody @Valid ProductKeyValidateRequest request
    ) {
        ProductKeyResponse data = productKeyService.verifyProductKeyByCode(request, true);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {SWAGGER_TAG_TOOL},
            value = "제품 키 검증",
            notes = "제품 키 가 정상적으로 발급되었는지 검증한다."
    )
    @Secured({PRODUCT_MANAGER})
    @PostMapping("/code/verifyCancel")
    public SingleResult<ProductKeyResponse> returnVerifyProductKey(
            @RequestBody @Valid ProductKeyValidateRequest request
    ) {
        ProductKeyResponse data = productKeyService.verifyProductKeyByCode(request, false);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {SWAGGER_TAG_TOOL},
            value = "제품 키 등록",
            notes = "제품 키 가 정상적으로 등록되었는지 확인한다."
    )
    @Secured({PRODUCT_MANAGER})
    @PostMapping("/code/assign")
    public SingleResult<ProductKeyResponse> assignedProductKey(
            @RequestBody @Valid ProductKeyValidateRequest request
    ) {
        ProductKeyResponse data = productKeyService.assignProductKeyByCode(request);
        return ResponseHelper.newSingleSuccessResult(data);
    }
}
