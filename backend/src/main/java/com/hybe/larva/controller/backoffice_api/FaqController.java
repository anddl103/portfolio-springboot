package com.hybe.larva.controller.backoffice_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.faq.*;
import com.hybe.larva.enums.Category;
import com.hybe.larva.service.FaqService;
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

@Api(tags = "Faq")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/faqs")
@RestController
public class FaqController {

    private final FaqService faqService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "Faq 추가",
            notes = "Faq를 추가한다."
    )
    @Secured({CS_MANAGER})
    @PostMapping("")
    public SingleResult<FaqResponse> addFaq(
            @RequestBody @Valid FaqAddRequest request
    ) {

        FaqResponse data = faqService.addFaq(request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "Faq 검색",
            notes = "선택한 조건으로 Faq를 검색한다."
    )
    @Secured({CS_MANAGER, VIEWER})
    @GetMapping("")
    public ListResult<FaqResponse> searchFaq(
            @ApiParam(value = "검색 시작일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime from,
            @ApiParam(value = "검색 종료일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime to,
            @ApiParam(value = "Offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @ApiParam(value = "category")
            @RequestParam(required = false) String category,
            @ApiParam(value = "keyword, like 검색")
            @RequestParam(required = false) String keyword
    ) {
        FaqSearchPageRequest request = FaqSearchPageRequest.builder()
                .from(from).to(to).offset(offset).limit(limit)
                .keyword(keyword)
                .build();
        Page<FaqResponse> data = faqService.searchFaq(request);

        List<FaqResponse> list = data.getContent();
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
    public SingleResult<FaqResponse> getFaq(
            @ApiParam(value = "Faq ID")
            @PathVariable String id
    ) {
        FaqResponse data = faqService.getFaq(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "Faq 수정",
            notes = "Faq를 수정한다."
    )
    @Secured({CS_MANAGER})
    @PatchMapping("/{id}")
    public SingleResult<FaqResponse> updateFaq(
            @ApiParam(value = "Faq ID")
            @PathVariable String id,
            @ApiParam(value = "업데이트 정보")
            @RequestBody @Valid FaqUpdateRequest request
    ) {
        FaqResponse data = faqService.updateFaq(id, request);
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
        faqService.deleteFaq(id);
        return ResponseHelper.newSuccessResult();
    }
}
