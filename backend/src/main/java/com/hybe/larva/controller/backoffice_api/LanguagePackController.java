package com.hybe.larva.controller.backoffice_api;


import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.language_pack.LanguagePackAddRequest;
import com.hybe.larva.dto.language_pack.LanguagePackResponse;
import com.hybe.larva.dto.language_pack.LanguagePackSearchRequest;
import com.hybe.larva.dto.language_pack.LanguagePackUpdateRequest;
import com.hybe.larva.service.LanguagePackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

import static com.hybe.larva.enums.UserRole.ROLES.*;

@Api(tags = "LanguagePack")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/languagePacks")
@RestController
public class LanguagePackController {

    private final LanguagePackService languagePackService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "언어 추가",
            notes = "언어를 추가한다."
    )
    @Secured({CONTENTS_EDITOR})
    @PostMapping("")
    public SingleResult<LanguagePackResponse> addNfcCard(
            @RequestBody @Valid LanguagePackAddRequest request
    ) {
        LanguagePackResponse data = languagePackService.addLanguagePack(request);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "언어 검색",
            notes = "선택한 조건으로 언어를 검색한다."
    )
    @Secured({CONTENTS_MANAGER, CONTENTS_EDITOR, PRODUCT_MANAGER, CS_MANAGER, VIEWER})
    @GetMapping("")
    public ListResult<LanguagePackResponse> searchLanguagePack(
            @ApiParam(value = "Offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @ApiParam(value = "keyword, like 검색")
            @RequestParam(required = false) String keyword
    ) {
        LanguagePackSearchRequest request = LanguagePackSearchRequest.builder()
                .offset(offset).limit(limit)
                .keyword(keyword)
                .build();
        Page<LanguagePackResponse> data = languagePackService.searchLanguagePack(request);

        List<LanguagePackResponse> list = data.getContent();
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
    public SingleResult<LanguagePackResponse> getLanguagePack(
            @ApiParam(value = "언어 ID")
            @PathVariable String id
    ) {
        LanguagePackResponse data = languagePackService.getLanguagePack(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "언어 수정",
            notes = "언어를 수정한다."
    )
    @Secured({CONTENTS_EDITOR})
    @PatchMapping("/{id}")
    public SingleResult<LanguagePackResponse> updateLanguagePack(
            @ApiParam(value = "언어 ID")
            @PathVariable String id,
            @ApiParam(value = "업데이트 정보")
            @RequestBody @Valid LanguagePackUpdateRequest request
    ) {
        LanguagePackResponse data = languagePackService.updateLanguagePack(id, request);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "언어 삭제",
            notes = "언어를 삭제한다."
    )
    @Secured({CONTENTS_EDITOR})
    @DeleteMapping("/{id}")
    public CommonResult deleteLanguagePack(
            @ApiParam(value = "언어 ID")
            @PathVariable String id
    ) {
        languagePackService.deleteLanguagePack(id);
        return ResponseHelper.newSuccessResult();
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "맴버(언어) 삭제",
            notes = "맴버를 삭제한다."
    )
    @Secured({CONTENTS_EDITOR})
    @PostMapping("/members")
    public CommonResult deleteLanguagePackMembers(
            @ApiParam(value = "맴버 Id, Array")
            @RequestBody List<String> languagePackIds
    ) {
        languagePackService.deleteLanguagePackMembers(languagePackIds);
        return ResponseHelper.newSuccessResult();
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "언어 Csv Download",
            notes = "언어를 Csv 파일로 다운로드한다."
    )
    @Secured({CONTENTS_EDITOR})
    @GetMapping("/file/download")
    public void getLanguagePackFile(
            HttpServletResponse response
    ) throws IOException {
        languagePackService.getLanguagePackDownload(response);
    }



    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "언어 추가",
            notes = "언어를 추가(등록/수정) 한다."
    )
    @Secured({CONTENTS_EDITOR})
    @PostMapping("/file/upload")
    public CommonResult mergeLanguagePackFile(
            @RequestPart MultipartFile file
    ) {
        languagePackService.mergeLanguagePack(file);
        return ResponseHelper.newSuccessResult();
    }



}
