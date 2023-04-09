package com.hybe.larva.controller.backoffice_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.question.*;
import com.hybe.larva.service.QuestionService;
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

@Api(tags = "Question")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/questions")
@RestController
public class QuestionController {

    private final QuestionService questionService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "1:1 문의 검색",
            notes = "선택한 조건으로 1:1 문의를 검색한다."
    )
    @Secured({CS_MANAGER, VIEWER})
    @GetMapping("")
    public ListResult<QuestionResponse> searchQuestionForBackoffice(
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
        QuestionSearchPageRequest request = QuestionSearchPageRequest.builder()
                .from(from).to(to).offset(offset).limit(limit)
                .build();
        Page<QuestionResponse> data = questionService.searchQuestion(request);

        List<QuestionResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "1:1 문의 조회",
            notes = "1:1 문의를 조회한다."
    )
    @Secured({CS_MANAGER, VIEWER})
    @GetMapping("/{id}")
    public SingleResult<QuestionResponse> getQuestionForBackoffice(
            @ApiParam(value = "1:1 문의 ID")
            @PathVariable String id
    ) {
        QuestionResponse data = questionService.getQuestion(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "1:1 문의 수정",
            notes = "1:1 문의를 수정한다."
    )
    @Secured({CS_MANAGER})
    @PatchMapping("/{id}")
    public SingleResult<QuestionResponse> updateQuestion(
            @ApiParam(value = "1:1 문의 ID")
            @PathVariable String id,
            @ApiParam(value = "업데이트 정보")
            @RequestBody @Valid QuestionUpdateRequest request
    ) {
        QuestionResponse data = questionService.updateQuestion(id, request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "1:1 문의 삭제",
            notes = "1:1 문의를 삭제한다."
    )
    @Secured({CS_MANAGER})
    @DeleteMapping("/{id}")
    public CommonResult deleteQuestion(
            @ApiParam(value = "1:1 문의 ID")
            @PathVariable String id
    ) {
        questionService.deleteQuestion(id);
        return ResponseHelper.newSuccessResult();
    }
}
