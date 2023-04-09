package com.hybe.larva.controller.client_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.faq_category.*;
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
@RequestMapping("${larva.api}/user/faqCategories")
@RestController
public class UserFaqCategoryController {

    private final FaqCategoryService faqCategoryService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "Faq Category 검색",
            notes = "선택한 조건으로 Faq Category를 검색한다."
    )
    @GetMapping("")
    public ListResult<UserFaqCategoryResponse> searchFaq(
            @ApiParam(value = "Offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        FaqCategorySearchRequest request = FaqCategorySearchRequest.builder()
                .offset(offset).limit(limit)
                .build();
        Page<UserFaqCategoryResponse> data = faqCategoryService.searchFaqCategoryForUser(request, null);

        List<UserFaqCategoryResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }
}
