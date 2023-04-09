package com.hybe.larva.controller.client_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.faq.FaqResponse;
import com.hybe.larva.dto.faq.FaqSearchRequest;
import com.hybe.larva.dto.faq.UserFaqResponse;
import com.hybe.larva.enums.Category;
import com.hybe.larva.service.FaqService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Faq")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/user/faqs")
@RestController
public class UserFaqController {

    private final FaqService faqService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "Faq 조회",
            notes = "Faq를 조회한다."
    )
    @GetMapping("/{id}")
    public SingleResult<UserFaqResponse> getFaq(
            @ApiParam(value = "Faq ID")
            @PathVariable String id
    ) {
        UserFaqResponse data = faqService.getFaqForUser(id, null);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "Faq 조회",
            notes = "Faq를 조회한다."
    )
    @GetMapping("/category/{category}")
    public ListResult<UserFaqResponse> getFaqCategory(
            @ApiParam(value = "category")
            @PathVariable String category,
            @ApiParam(value = "offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @ApiParam(value = "언어코드")
            @RequestParam(required = false, defaultValue = "")  String lang
    ) {
        FaqSearchRequest request = FaqSearchRequest.builder()
                .limit(limit)
                .offset(offset)
                .build();

        Page<UserFaqResponse> data = faqService.getFaqCategoryForUser(category, request, lang);

        List<UserFaqResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);


        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    /*
    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "Faq (컨텐츠 적용) 조회",
            notes = "Faq (컨텐츠 적용)을 조회한다."
    )
    @GetMapping("/category/applyContent")
    public ListResult<UserFaqResponse> getFaqCategoryApplyContent(
            @ApiParam(value = "offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        FaqSearchRequest request = FaqSearchRequest.builder()
                .limit(limit)
                .offset(offset)
                .build();

        Page<UserFaqResponse> data = faqService.getFaqCategory(Category.APPLY_CONTENT, request);

        List<UserFaqResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "Faq (포토카드) 조회",
            notes = "Faq (포토카드)를 조회한다."
    )
    @GetMapping("/category/photoCard")
    public ListResult<UserFaqResponse> getFaqCategoryPhotoCard(
            @ApiParam(value = "offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {

        FaqSearchRequest request = FaqSearchRequest.builder()
                .limit(limit)
                .offset(offset)
                .build();

        Page<UserFaqResponse> data = faqService.getFaqCategory(Category.PHOTO_CARD, request);

        List<UserFaqResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "Faq (계정 관련) 조회",
            notes = "Faq (계정 관련)을 조회한다."
    )
    @GetMapping("/category/account")
    public ListResult<UserFaqResponse> getFaqCategoryAccount(
            @ApiParam(value = "offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        FaqSearchRequest request = FaqSearchRequest.builder()
                .limit(limit)
                .offset(offset)
                .build();
        Page<UserFaqResponse> data = faqService.getFaqCategory(Category.ACCOUNT, request);

        List<UserFaqResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "Faq (앱 사용 방법) 조회",
            notes = "Faq (앱 사용 방법)을 조회한다."
    )
    @GetMapping("/category/app")
    public ListResult<FaqResponse> getFaqCategoryApp(
            @ApiParam(value = "offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        FaqSearchRequest request = FaqSearchRequest.builder()
                .limit(limit)
                .offset(offset)
                .build();
        Page<UserFaqResponse> data = faqService.getFaqCategory(Category.APP, request);

        List<UserFaqResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }
    */

}
