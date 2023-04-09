package com.hybe.larva.controller.backoffice_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.artist.*;
import com.hybe.larva.dto.common.*;
import com.hybe.larva.service.ArtistService;
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
@Api(tags = "Artist")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/artists")
@RestController
public class ArtistController {

    private final ArtistService artistService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "아티스트 추가",
            notes = "아티스트를 추가한다."
    )
    @Secured({CONTENTS_EDITOR})
    @PostMapping("")
    public SingleResult<ArtistDetailResponse> addArtist(
            @RequestBody @Valid ArtistAddRequest request
    ) {
        ArtistDetailResponse data = artistService.addArtist(request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "아티스트 검색",
            notes = "선택한 조건으로 아티스트를 검색한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER, PRODUCT_MANAGER, VIEWER})
    @GetMapping("")
    public ListResult<ArtistSearchResponse> searchArtistForBackOffice(
            @ApiParam(value = "검색 시작일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime from,
            @ApiParam(value = "검색 종료일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime to,
            @ApiParam(value = "페이지 번호")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @ApiParam(value = "이름, like 검색")
            @RequestParam(required = false) String name,
            @ApiParam(value = "공개 여부")
            @RequestParam(required = false) Boolean display

    ) {
        ArtistSearchRequest request = ArtistSearchRequest.builder()
                .from(from).to(to)
                .offset(offset)
                .limit(limit)
                .name(name)
                .display(display)
                .build();
        Page<ArtistSearchResponse> data = artistService.searchArtist(request);

        List<ArtistSearchResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "아티스트 조회",
            notes = "아티스트를 조회한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER, PRODUCT_MANAGER, VIEWER})
    @GetMapping("/{id}")
    public SingleResult<ArtistDetailResponse> getArtist(
            @ApiParam(value = "아티스트 ID")
            @PathVariable String id
    ) {
        ArtistDetailResponse data = artistService.getArtistForBackOffice(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "아티스트 수정",
            notes = "아티스트를 수정한다."
    )
    @Secured({CONTENTS_EDITOR})
    @PatchMapping("/{id}")
    public SingleResult<ArtistDetailResponse> updateArtist(
            @ApiParam(value = "아티스트 ID")
            @PathVariable String id,
            @ApiParam(value = "업데이트 정보")
            @RequestBody @Valid ArtistUpdateRequest request
    ) {
        ArtistDetailResponse data = artistService.updateArtist(id, request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "아티스트 삭제",
            notes = "아티스트를 삭제한다."
    )
    @Secured({CONTENTS_EDITOR})
    @DeleteMapping("/{id}")
    public CommonResult deleteArtist(
            @ApiParam(value = "아티스트 ID")
            @PathVariable String id
    ) {
        artistService.deleteArtist(id);
        return ResponseHelper.newSuccessResult();
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "중복 검사",
            notes = "중복되는지 검사한다."
    )
    @Secured({CONTENTS_EDITOR})
    @GetMapping("/duplicate-check")
    public SingleResult<DuplicationCheckResponse> duplicateCheck(
            @ApiParam(value = "이름")
            @RequestParam String name,
            @ApiParam(value = "아티스트 Id")
            @RequestParam(required = false) String id
    )  {
        boolean duplicated = artistService.findByName(name, id);
        DuplicationCheckResponse data = new DuplicationCheckResponse(duplicated);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "아티스트 순서 수정",
            notes = "아티스트의 순서를 수정한다."
    )
    @Secured({CONTENTS_EDITOR})
    @PatchMapping("/sort")
    public CommonResult updateArtistSortOrder(
            @ApiParam(value = "업데이트 정보")
            @RequestBody @Valid ArtistUpdateSortOrderRequest request
    ) {
        artistService.updateArtistSortOrder(request);
        return ResponseHelper.newSuccessResult();
    }
}
