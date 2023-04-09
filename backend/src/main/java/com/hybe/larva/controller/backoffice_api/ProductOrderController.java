package com.hybe.larva.controller.backoffice_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;

import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.product_order.ProductOrderAddRequest;
import com.hybe.larva.dto.product_order.ProductOrderResponse;
import com.hybe.larva.dto.product_order.ProductOrderSearchRequest;
import com.hybe.larva.dto.product_order.ProductOrderUpdateRequest;
import com.hybe.larva.enums.ProductOrderStatus;
import com.hybe.larva.service.ProductOrderService;
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

@Api(tags = "ProductOrder")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/productOrders")
@RestController
public class ProductOrderController {

    private final ProductOrderService productOrderService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "상품 주문 추가",
            notes = "상품 주문을 추가한다."
    )
    @Secured({PRODUCT_MANAGER})
    @PostMapping("")
    public SingleResult<ProductOrderResponse> addProductOrder(
            @RequestBody @Valid ProductOrderAddRequest request
    ) {
        ProductOrderResponse data = productOrderService.addProductOrder(request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "상품 주문 검색",
            notes = "선택한 조건으로 상품 주문을 검색한다."
    )
    @Secured({PRODUCT_MANAGER, VIEWER})
    @GetMapping("")
    public ListResult<ProductOrderResponse> searchProductOrder(
            @ApiParam(value = "검색 시작일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime from,
            @ApiParam(value = "검색 종료일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime to,
            @ApiParam(value = "Offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @ApiParam(value = "아티스트 ID")
            @RequestParam(required = false) String artistId,
            @ApiParam(value = "주문 상태")
            @RequestParam(required = false) ProductOrderStatus productOrderStatus
    ) {
        ProductOrderSearchRequest request = ProductOrderSearchRequest.builder()
                .from(from).to(to).offset(offset).limit(limit).artistId(artistId)
                .productOrderStatus(productOrderStatus)
                .build();
        Page<ProductOrderResponse> data = productOrderService.searchProductOrder(request);

        List<ProductOrderResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "상품 주문 조회",
            notes = "상품 주문을 조회한다."
    )
    @Secured({PRODUCT_MANAGER, VIEWER})
    @GetMapping("/{id}")
    public SingleResult<ProductOrderResponse> getProductOrder(
            @ApiParam(value = "상품 주문 ID")
            @PathVariable String id
    ) {
        ProductOrderResponse data = productOrderService.getProductOrder(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "상품 주문 수정",
            notes = "상품 주문을 수정한다."
    )
    @Secured({PRODUCT_MANAGER})
    @PatchMapping("/{id}")
    public SingleResult<ProductOrderResponse> updateProductOrder(
            @ApiParam(value = "상품 주문 ID")
            @PathVariable String id,
            @ApiParam(value = "업데이트 정보")
            @RequestBody @Valid ProductOrderUpdateRequest request
    ) {
        ProductOrderResponse data = productOrderService.updateProductOrder(id, request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "상품 주문 삭제",
            notes = "상품 주문을 삭제한다."
    )
    @Secured({PRODUCT_MANAGER})
    @DeleteMapping("/{id}")
    public CommonResult deleteProductOrder(
            @ApiParam(value = "상품 주문 ID")
            @PathVariable String id
    ) {
        productOrderService.deleteProductOrder(id);
        return ResponseHelper.newSuccessResult();
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "주문 완료 처리",
            notes = "해당 주문의 생산이 완료된 경우 주문을 닫는다."
    )
    @Secured({PRODUCT_MANAGER})
    @PatchMapping("/{id}/complete")
    public SingleResult<ProductOrderResponse> completeProductOrder(
            @ApiParam(value = "주문 ID")
            @PathVariable String id
    ) {
        ProductOrderResponse data = productOrderService.completeProductOrder(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "주문 취소 처리",
            notes = "해당 주문의 생산이 취소로 변경된다."
    )
    @Secured({PRODUCT_MANAGER})
    @PatchMapping("/{id}/cancel")
    public SingleResult<ProductOrderResponse> progressProductOrder(
            @ApiParam(value = "주문 ID")
            @PathVariable String id
    ) {
        ProductOrderResponse data = productOrderService.cancelledProductOrder(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }
}
