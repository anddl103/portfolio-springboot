package com.hybe.larva.controller.backoffice_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.banner.*;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.storage.PreSignedUrlResponse;
import com.hybe.larva.exception.ResourceNotFoundException;
import com.hybe.larva.service.BannerService;
import com.hybe.larva.service.StorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.List;

import static com.hybe.larva.enums.UserRole.ROLES.*;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Slf4j
@Api(tags = "Banner")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/banners")
@RestController
public class BannerController {

    private final BannerService bannerService;
    private final StorageService storageService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "배너 추가",
            notes = "배너를 추가한다."
    )
    @Secured({CONTENTS_EDITOR})
    @PostMapping("")
    public SingleResult<BannerDetailResponse> addBanner(
            @RequestBody @Valid BannerAddRequest request
    ) {
        BannerDetailResponse data = bannerService.addBanner(request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "배너 검색",
            notes = "선택한 조건으로 배너를 검색한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER, VIEWER})
    @GetMapping("/search")
    public ListResult<BannerResponse> searchBannerForBackoffice(
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
            @ApiParam(value = "albumId, 검색")
            @RequestParam(required = false) String albumId,
            @ApiParam(value = "artistId, 검색")
            @RequestParam(required = false) String artistId
    ) {
        BannerSearchPageRequest request = BannerSearchPageRequest.builder()
                .from(from).to(to).offset(offset).limit(limit)
                .keyword(keyword)
                .albumId(albumId)
                .artistId(artistId)
                .build();
        Page<BannerResponse> data = bannerService.searchBannerForBackoffice(request);

        List<BannerResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "배너 조회",
            notes = "배너를 조회한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER, VIEWER})
    @GetMapping("/{id}")
    public SingleResult<BannerDetailResponse> getBannerForBackoffice(
            @ApiParam(value = "배너 ID")
            @PathVariable String id
    ) {
        BannerDetailResponse data = bannerService.getBannerForBackoffice(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "배너 수정",
            notes = "배너를 수정한다."
    )
    @Secured({CONTENTS_EDITOR})
    @PatchMapping("/{id}")
    public SingleResult<BannerDetailResponse> updateBanner(
            @ApiParam(value = "배너 ID")
            @PathVariable String id,
            @ApiParam(value = "업데이트 정보")
            @RequestBody @Valid BannerUpdateRequest request
    ) {
        BannerDetailResponse data = bannerService.updateBanner(id, request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "배너 삭제",
            notes = "배너를 삭제한다."
    )
    @Secured({CONTENTS_EDITOR})
    @DeleteMapping("/{id}")
    public CommonResult deleteBanner(
            @ApiParam(value = "배너 ID")
            @PathVariable String id
    ) {
        bannerService.deleteBanner(id);
        return ResponseHelper.newSuccessResult();
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "배너 순서 수정",
            notes = "배너의 순서를 수정한다."
    )
    @Secured({CONTENTS_EDITOR})
    @PatchMapping("/sort")
    public CommonResult updateBannerSortOrder(
            @ApiParam(value = "업데이트 정보")
            @RequestBody @Valid BannerUpdateSortOrderRequest request
    ) {
        bannerService.updateBannerSortOrder(request);
        return ResponseHelper.newSuccessResult();
    }

}
