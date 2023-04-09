package com.hybe.larva.controller.backoffice_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.notice.*;
import com.hybe.larva.service.NoticeService;
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

@Api(tags = "Notice")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/notices")
@RestController
public class NoticeController {

    private final NoticeService noticeService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "공지사항 추가",
            notes = "공지사항을 추가한다."
    )
    @Secured({CS_MANAGER})
    @PostMapping("")
    public SingleResult<NoticeResponse> addNotice(
            @RequestBody @Valid NoticeAddRequest request
    ) {

        NoticeResponse data = noticeService.addNotice(request);
        return ResponseHelper.newSingleSuccessResult(data);
    }



    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "공지사항 조회",
            notes = "공지사항을 조회한다."
    )
    @GetMapping("/{id}")
    @Secured({CS_MANAGER, VIEWER})
    public SingleResult<NoticeResponse> getNotice(
            @ApiParam(value = "Notice ID")
            @PathVariable String id
    ) {
        NoticeResponse data = noticeService.getNotice(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "공지사항 수정",
            notes = "공지사항을 수정한다."
    )
    @Secured({CS_MANAGER})
    @PatchMapping("/{id}")
    public SingleResult<NoticeResponse> updateNotice(
            @ApiParam(value = "공지사항 ID")
            @PathVariable String id,
            @ApiParam(value = "업데이트 정보")
            @RequestBody @Valid NoticeUpdateRequest request
    ) {
        NoticeResponse data = noticeService.updateNotice(id, request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "공지사항 삭제",
            notes = "공지사항을 삭제한다."
    )
    @Secured({CS_MANAGER})
    @DeleteMapping("/{id}")
    public CommonResult deleteNotice(
            @ApiParam(value = "공지사항 ID")
            @PathVariable String id
    ) {
        noticeService.deleteNotice(id);
        return ResponseHelper.newSuccessResult();
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "공지사항 검색",
            notes = "선택한 조건으로 공지사항을 검색한다."
    )
    @Secured({CS_MANAGER, VIEWER})
    @GetMapping("")
    public ListResult<NoticeResponse> searchNotice(
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
        NoticeSearchPageRequest request = NoticeSearchPageRequest.builder()
                .from(from).to(to).offset(offset).limit(limit)
                .keyword(keyword)
                .build();
        Page<NoticeResponse> data = noticeService.searchNotice(request);

        List<NoticeResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }
}
