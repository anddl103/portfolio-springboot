package com.hybe.larva.controller.backoffice_api;

import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.enums.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

import static com.hybe.larva.consts.LarvaConst.SWAGGER_TAG_ADMIN;
import static com.hybe.larva.enums.UserRole.ROLES.*;


@Api(tags = "Code")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/codes")
@RestController
@Slf4j
public class CodeController {

    @ApiOperation(
            tags = {SWAGGER_TAG_ADMIN},
            value = "에러 코드 조회",
            notes = "에러 코드를 조회한다."
    )
    @Secured({SUPER_ADMIN, CONTENTS_MANAGER, CONTENTS_EDITOR, PRODUCT_MANAGER, CS_MANAGER, VIEWER})
    @GetMapping("/error")
    public ListResult<ErrorCode> errorCode() {
        return ResponseHelper.newListSuccessResult(Arrays.asList(ErrorCode.values()));
    }

    @ApiOperation(
            tags = {SWAGGER_TAG_ADMIN},
            value = "주문 상태 코드 조회",
            notes = "주문 상태 코드를 조회한다."
    )
    @Secured({PRODUCT_MANAGER, VIEWER})
    @GetMapping("/product/order/status")
    public ListResult<ProductOrderStatus> productOrderState() {
        return ResponseHelper.newListSuccessResult(Arrays.asList(ProductOrderStatus.values()));
    }

    @ApiOperation(
            tags = {SWAGGER_TAG_ADMIN},
            value = "관리자 역할 코드 조회",
            notes = "관리자 역할 코드를 조회한다."
    )
    @Secured({SUPER_ADMIN, CONTENTS_MANAGER, CONTENTS_EDITOR, PRODUCT_MANAGER, VIEWER, CS_MANAGER})
    @GetMapping("/admin/role")
    public ListResult<UserRole> userRole() {
        return ResponseHelper.newListSuccessResult(Arrays.asList(UserRole.values()));
    }

    @ApiOperation(
            tags = {SWAGGER_TAG_ADMIN},
            value = "컨텐츠 타입 조회",
            notes = "컨텐츠 타입을 조회한다."
    )
    @Secured({CONTENTS_MANAGER, CONTENTS_EDITOR, VIEWER})
    @GetMapping("/contents-type")
    public ListResult<ContentsType> contentsType() {
        return ResponseHelper.newListSuccessResult(Arrays.asList(ContentsType.values()));
    }

    @ApiOperation(
            tags = {SWAGGER_TAG_ADMIN},
            value = "서비스 지역 조회",
            notes = "서비스 지역을 조회한다."
    )
    @Secured({PRODUCT_MANAGER, CS_MANAGER, VIEWER})
    @GetMapping("/region")
    public ListResult<ServiceRegion> getRegion() {
        return ResponseHelper.newListSuccessResult(Arrays.asList(ServiceRegion.values()));
    }

    @ApiOperation(
            tags = {SWAGGER_TAG_ADMIN},
            value = "정책 사용처 조회",
            notes = "정책 사용처를 조회한다."
    )
    @Secured({CS_MANAGER, VIEWER})
    @GetMapping("/usage")
    public ListResult<Usage> getUsage() {
        return ResponseHelper.newListSuccessResult(Arrays.asList(Usage.values()));
    }

    @ApiOperation(
            tags = {SWAGGER_TAG_ADMIN},
            value = "Faq 카테고리 조회",
            notes = "Faq 카테고리를 조회한다."
    )
    @Secured({CS_MANAGER, VIEWER})
    @GetMapping("/category")
    public ListResult<Category> getCategory() {
        return ResponseHelper.newListSuccessResult(Arrays.asList(Category.values()));
    }

    @ApiOperation(
            tags = {SWAGGER_TAG_ADMIN},
            value = "쿼리 유형 조회",
            notes = "쿼리 유형을 조회한다."
    )
    @Secured({CONTENTS_MANAGER, CONTENTS_EDITOR, VIEWER})
    @GetMapping("/operation-type")
    public ListResult<OperationType> operationType() {
        return ResponseHelper.newListSuccessResult(Arrays.asList(OperationType.values()));
    }

    @ApiOperation(
            tags = {SWAGGER_TAG_ADMIN},
            value = "앨범 상태 코드 조회",
            notes = "앨범 상태 코드를 조회한다."
    )
    @Secured({CONTENTS_MANAGER, CONTENTS_EDITOR, VIEWER})
    @GetMapping("/album-state")
    public ListResult<AlbumState> albumState() {
        return ResponseHelper.newListSuccessResult(Arrays.asList(AlbumState.values()));
    }

}
