package com.hybe.larva.controller.backoffice_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.album.*;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.exception.ProtectedResourceException;
import com.hybe.larva.exception.ResourceNotFoundException;
import com.hybe.larva.service.AlbumService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static com.hybe.larva.enums.UserRole.ROLES.*;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Slf4j
@Api(tags = "Album")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/albums")
@RestController
public class AlbumController {

    private final AlbumService albumService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "앨범 검색",
            notes = "선택한 조건으로 앨범을 검색한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER, PRODUCT_MANAGER, VIEWER})
    @GetMapping("")
    public ListResult<AlbumResponse> searchAlbum(
            @ApiParam(value = "검색 시작일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime from,
            @ApiParam(value = "검색 종료일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime to,
            @ApiParam(value = "Offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @ApiParam(value = "keyword, like 검색")
            @RequestParam(required = false) String keyword,
            @ApiParam(value = "아티스트 id")
            @RequestParam(required = false) String artistId
    ) {
        AlbumSearchRequest request = AlbumSearchRequest.builder()
                .from(from).to(to).offset(offset).limit(limit)
                .keyword(keyword).artistId(artistId)
                .build();
        Page<AlbumResponse> data = albumService.searchAlbum(request);

        List<AlbumResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "앨범 조회",
            notes = "앨범을 조회한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER, PRODUCT_MANAGER, VIEWER})
    @GetMapping("/{id}")
    public SingleResult<AlbumDetailResponse> getAlbum(
            @ApiParam(value = "앨범 ID")
            @PathVariable String id
    ) {

        AlbumDetailResponse data = albumService.getAlbum(id);

        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "앨범내 카드 조회",
            notes = "앨범내 카드를 조회한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER, PRODUCT_MANAGER, VIEWER})
    @GetMapping("/{id}/cards")
    public ListResult<CardResponse> getAlbumCard(
            @ApiParam(value = "앨범 ID")
            @PathVariable String id
    ) {
        List<CardResponse> data = albumService.getAlbumCard(id);

        return ResponseHelper.newListSuccessResult(data);
    }
}
