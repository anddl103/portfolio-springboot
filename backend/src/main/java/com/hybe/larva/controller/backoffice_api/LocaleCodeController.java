package com.hybe.larva.controller.backoffice_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.locale_code.LocaleCodeAddRequest;
import com.hybe.larva.dto.locale_code.LocaleCodeResponse;
import com.hybe.larva.dto.locale_code.LocaleCodeSearchRequest;
import com.hybe.larva.dto.locale_code.LocaleCodeUpdateRequest;
import com.hybe.larva.service.LocaleCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static com.hybe.larva.enums.UserRole.ROLES.*;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Api(tags = "LocaleCode")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/countryCodes")
@RestController
public class LocaleCodeController {

    private final LocaleCodeService localeCodeService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "언어 추가",
            notes = "언어를 추가한다."
    )
    @Secured({CONTENTS_EDITOR})
    @PostMapping("")
    public SingleResult<LocaleCodeResponse> addLocaleCode(
            @RequestBody @Valid LocaleCodeAddRequest request
    ) {

        LocaleCodeResponse data = localeCodeService.addLocaleCode(request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "언어 검색",
            notes = "선택한 조건으로 언어를 검색한다."
    )
    @Secured({CONTENTS_MANAGER, CONTENTS_EDITOR, PRODUCT_MANAGER, CS_MANAGER, VIEWER})
    @GetMapping("")
    public ListResult<LocaleCodeResponse> searchLocaleCode(
            @ApiParam(value = "검색 시작일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime from,
            @ApiParam(value = "검색 종료일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime to,
            @ApiParam(value = "Offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @ApiParam(value = "이름, like 검색")
            @RequestParam(required = false) String name
    ) {
        LocaleCodeSearchRequest request = LocaleCodeSearchRequest.builder()
                .from(from).to(to).offset(offset).limit(limit)
                .build();
        Page<LocaleCodeResponse> data = localeCodeService.searchLocaleCode(request);

        List<LocaleCodeResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "언어 조회",
            notes = "언어를 조회한다."
    )
    @Secured({CONTENTS_MANAGER, CONTENTS_EDITOR, PRODUCT_MANAGER, CS_MANAGER, VIEWER})
    @GetMapping("/{id}")
    public SingleResult<LocaleCodeResponse> getLocaleCode(
            @ApiParam(value = "언어 ID")
            @PathVariable String id
    ) {
        LocaleCodeResponse data = localeCodeService.getLocaleCode(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "언어 수정",
            notes = "언어를 수정한다."
    )
    @Secured({CONTENTS_EDITOR})
    @PatchMapping("/{id}")
    public SingleResult<LocaleCodeResponse> updateLocaleCode(
            @ApiParam(value = "언어 ID")
            @PathVariable String id,
            @ApiParam(value = "업데이트 정보")
            @RequestBody @Valid LocaleCodeUpdateRequest request
    ) {
        LocaleCodeResponse data = localeCodeService.updateLocaleCode(id, request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "언어 삭제",
            notes = "언어를 삭제한다."
    )
    @Secured({CONTENTS_EDITOR})
    @DeleteMapping("/{id}")
    public CommonResult deleteLocaleCode(
            @ApiParam(value = "언어 ID")
            @PathVariable String id
    ) {
        localeCodeService.deleteLocaleCode(id);
        return ResponseHelper.newSuccessResult();
    }

}
