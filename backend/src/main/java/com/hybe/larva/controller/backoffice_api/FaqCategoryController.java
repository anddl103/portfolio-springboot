package com.hybe.larva.controller.backoffice_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.faq.FaqAddRequest;
import com.hybe.larva.dto.faq.FaqSearchPageRequest;
import com.hybe.larva.dto.faq.FaqUpdateRequest;
import com.hybe.larva.dto.faq_category.FaqCategoryAddRequest;
import com.hybe.larva.dto.faq_category.FaqCategoryResponse;
import com.hybe.larva.dto.faq_category.FaqCategorySearchRequest;
import com.hybe.larva.dto.faq_category.FaqCategoryUpdateRequest;
import com.hybe.larva.entity.faq_category.FaqCategory;
import com.hybe.larva.service.FaqCategoryService;
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

@Api(tags = "FaqCategory")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/faqCategories")
@RestController
public class FaqCategoryController {

    private final FaqCategoryService faqCategoryService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "Faq 추가",
            notes = "Faq를 추가한다."
    )
    @Secured({CS_MANAGER})
    @PostMapping("")
    public SingleResult<FaqCategoryResponse> addFaq(
            @RequestBody @Valid FaqCategoryAddRequest request
    ) {

        FaqCategoryResponse data = faqCategoryService.addFaqCategory(request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "Faq 검색",
            notes = "선택한 조건으로 Faq를 검색한다."
    )
    @Secured({CS_MANAGER, VIEWER})
    @GetMapping("")
    public ListResult<FaqCategoryResponse> searchFaq(
            @ApiParam(value = "검색 시작일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime from,
            @ApiParam(value = "검색 종료일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime to,
            @ApiParam(value = "Offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @ApiParam(value = "keyword, like 검색")
            @RequestParam(required = false) String keyword
    ) {
        FaqCategorySearchRequest request = FaqCategorySearchRequest.builder()
                .from(from).to(to).offset(offset).limit(limit)
                .keyword(keyword)
                .build();
        Page<FaqCategoryResponse> data = faqCategoryService.searchFaqCategory(request);

        List<FaqCategoryResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "Faq 조회",
            notes = "Faq를 조회한다."
    )
    @Secured({CS_MANAGER, VIEWER})
    @GetMapping("/{id}")
    public SingleResult<FaqCategoryResponse> getFaq(
            @ApiParam(value = "Faq ID")
            @PathVariable String id
    ) {
        FaqCategoryResponse data = faqCategoryService.getFaqCategory(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "Faq 수정",
            notes = "Faq를 수정한다."
    )
    @Secured({CS_MANAGER})
    @PatchMapping("/{id}")
    public SingleResult<FaqCategoryResponse> updateFaq(
            @ApiParam(value = "Faq ID")
            @PathVariable String id,
            @ApiParam(value = "업데이트 정보")
            @RequestBody @Valid FaqCategoryUpdateRequest request
    ) {
        FaqCategoryResponse data = faqCategoryService.updateFaqCategory(id, request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "Faq 삭제",
            notes = "Faq를 삭제한다."
    )
    @Secured({CS_MANAGER})
    @DeleteMapping("/{id}")
    public CommonResult deleteFaq(
            @ApiParam(value = "Faq ID")
            @PathVariable String id
    ) {
        faqCategoryService.deleteFaqCategory(id);
        return ResponseHelper.newSuccessResult();
    }
}
