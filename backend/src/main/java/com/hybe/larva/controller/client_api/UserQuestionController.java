package com.hybe.larva.controller.client_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.question.QuestionAddRequest;
import com.hybe.larva.dto.question.QuestionSearchRequest;
import com.hybe.larva.dto.question.UserQuestionResponse;
import com.hybe.larva.service.QuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.hybe.larva.enums.UserRole.ROLES.USER;

@Api(tags = "Question")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/user/questions")
@RestController
public class UserQuestionController {

    private final QuestionService questionService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "1:1 문의 추가",
            notes = "1:1 문의를 추가한다."
    )
    @Secured({USER})
    @PostMapping("")
    public SingleResult<UserQuestionResponse> addQuestion(
            @RequestBody @Valid QuestionAddRequest request
    ) {
        UserQuestionResponse data = questionService.addQuestionForUser(request);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "1:1 문의 검색",
            notes = "선택한 조건으로 1:1 문의를 검색한다."
    )
    @Secured({USER})
    @GetMapping("")
    public ListResult<UserQuestionResponse> searchUserQuestion(
            @ApiParam(value = "offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        QuestionSearchRequest request = QuestionSearchRequest.builder()
                .offset(offset)
                .limit(limit)
                .build();
        Page<UserQuestionResponse> data = questionService.searchQuestionForUser(request);

        List<UserQuestionResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "1:1 문의 조회",
            notes = "1:1 문의를 조회한다."
    )
    @Secured({USER})
    @GetMapping("/{id}")
    public SingleResult<UserQuestionResponse> getQuestion(
            @ApiParam(value = "1:1 문의 ID")
            @PathVariable String id
    ) {
        UserQuestionResponse data = questionService.getQuestionForUser(id, CommonUser.getUid());
        return ResponseHelper.newSingleSuccessResult(data);
    }
}
