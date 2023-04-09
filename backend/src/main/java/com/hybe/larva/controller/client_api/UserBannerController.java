package com.hybe.larva.controller.client_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.banner.*;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.storage.PreSignedUrlResponse;
import com.hybe.larva.entity.banner.BannerContents;
import com.hybe.larva.exception.AuthException;
import com.hybe.larva.exception.ResourceNotFoundException;
import com.hybe.larva.service.BannerService;
import com.hybe.larva.service.StorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;

import static com.hybe.larva.enums.UserRole.ROLES.*;

@Slf4j
@Api(tags = "Banner")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/banners")
@RestController
public class UserBannerController {

    private final BannerService bannerService;
    private final StorageService storageService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "배너 검색",
            notes = "선택한 조건으로 배너를 검색한다."
    )
    @Secured({USER})
    @GetMapping("")
    public ListResult<BannerSearchResponse> searchUserBanner(
            @ApiParam(value = "offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @ApiParam(value = "아티스트 Id")
            @RequestParam(required = false) String artistId
    ) {
        BannerSearchRequest request = BannerSearchRequest.builder()
                .offset(offset)
                .limit(limit)
                .artistId(artistId)
                .build();
        Page<BannerSearchResponse> data = bannerService.searchBanner(request);

        List<BannerSearchResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "배너 link 이동",
            notes = "배너 link 이동한다."
    )
    @GetMapping("/{id}/link")
    public ResponseEntity<Object> link(
            @ApiParam(value = "배너 ID")
            @PathVariable String id
    ) throws URISyntaxException, IOException, InvalidKeySpecException {

        BannerSearchResponse bannerSearchResponse = bannerService.getBanner(id);

        return ResponseHelper.newContentsSuccessRedirect(bannerSearchResponse.getContents().getLink());
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "배너 thumbnail 임시 다운로드 URL 생성 및 이동",
            notes = "AWS S3에 업로드된 파일의 임시 다운로드 URL을 생성 후 이동한다."
    )
    @Secured({USER})
    @GetMapping("/{id}/thumbnail")
    public ResponseEntity<Object> thumbnailPreSignedUrl(
            @ApiParam(value = "배너 ID")
            @PathVariable String id,
            @ApiParam(value = "만료 시간(초)")
            @RequestParam(required = false, defaultValue = "600") @Min(0) Long expSeconds
    ) throws URISyntaxException, IOException, InvalidKeySpecException {

        BannerSearchResponse bannerSearchResponse = bannerService.getBanner(id);

        if (bannerSearchResponse.getThumbnailKey() == null) {
            throw new ResourceNotFoundException("Cannot find Thumbnail id=" + id);
        }


        PreSignedUrlResponse data =
                storageService.generatePreSignedUrl(bannerSearchResponse.getThumbnailKey(), expSeconds);

        if (data == null) {
            throw new ResourceNotFoundException("Cannot find Thumbnail Storage id=" + id);
        }

        return ResponseHelper.newContentsSuccessRedirect(data.getUrl());
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "배너 contents 다운로드 URL 생성 및 이동",
            notes = "AWS S3에 업로드된 파일의 임시 다운로드 URL을 생성 후 이동한다."
    )
    @Secured({USER})
    @GetMapping("/{id}/contents")
    public ResponseEntity<Object> contentsPreSignedUrl(
            @ApiParam(value = "배너 ID")
            @PathVariable String id,
            @ApiParam(value = "contents key")
            @RequestParam String key,
            @ApiParam(value = "만료 시간(초)")
            @RequestParam(required = false, defaultValue = "3600") @Min(0) Long expSeconds
    ) throws URISyntaxException, IOException, InvalidKeySpecException {

        BannerDetailResponse bannerDetailResponse = bannerService.getBannerForBackoffice(id);
        Map<String, BannerContents> contentsMap = bannerDetailResponse.getContents();

        PreSignedUrlResponse data = null;

        if (contentsMap != null && !contentsMap.isEmpty()) {
            for (Map.Entry<String, BannerContents> entry : contentsMap.entrySet()) {

                if (entry.getValue() != null &&
                        entry.getValue().getImage() != null &&
                        entry.getValue().getImage().equals(key)) {
                    data = storageService.generatePreSignedUrl(key, expSeconds);
                }
            }
        }


        if (data == null) {
            throw new ResourceNotFoundException("Cannot find contents banner id=" + id);
        }

        return ResponseHelper.newContentsSuccessRedirect(data.getUrl());
    }

}
