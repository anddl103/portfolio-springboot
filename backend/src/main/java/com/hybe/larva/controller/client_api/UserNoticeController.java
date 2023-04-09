package com.hybe.larva.controller.client_api;

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
@RequestMapping("${larva.api}/user/notices")
@RestController
public class UserNoticeController {

    private final NoticeService noticeService;


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "공지사항 검색",
            notes = "선택한 조건으로 공지사항을 검색한다."
    )
    @GetMapping("")
    public ListResult<UserNoticeResponse> searchNotice(
            @ApiParam(value = "offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        NoticeSearchRequest request = NoticeSearchRequest.builder()
                .offset(offset)
                .limit(limit)
                .build();
        Page<UserNoticeResponse> data = noticeService.searchNoticeForUser(request);

        List<UserNoticeResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "공지사항 조회",
            notes = "공지사항을 조회한다."
    )
    @GetMapping("/{id}")
    public SingleResult<UserNoticeResponse> getNotice(
            @ApiParam(value = "Notice ID")
            @PathVariable String id
    ) {
        UserNoticeResponse data = noticeService.getNoticeForUser(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }
}
