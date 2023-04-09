package com.hybe.larva.controller.client_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.storage.PreSignedUrlResponse;
import com.hybe.larva.dto.user_artist.UserArtistSearchRequest;
import com.hybe.larva.dto.user_artist.UserArtistSearchResponse;
import com.hybe.larva.exception.ResourceNotFoundException;
import com.hybe.larva.service.StorageService;
import com.hybe.larva.service.UserArtistService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import static com.hybe.larva.enums.UserRole.ROLES.*;

@Api(tags = "UserArtist")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/userArtists")
@RestController
public class UserArtistController {

    private final UserArtistService userArtistService;
    private final StorageService storageService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "유저 아티스트 조회",
            notes = "유저 아티스트를 조회한다."
    )
    @Secured({USER})
    @GetMapping("/{id}")
    public SingleResult<UserArtistSearchResponse> getUserArtist(
            @ApiParam(value = "유저 아티스트 ID")
            @PathVariable String id
    ) {
        UserArtistSearchResponse data = userArtistService.getUserArtist(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "유저 아티스트 검색",
            notes = "선택한 조건으로 유저 아티스트를 검색한다."
    )
    @Secured({USER})
    @GetMapping("")
    public ListResult<UserArtistSearchResponse> searchUserArtist(
            @ApiParam(value = "offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        UserArtistSearchRequest request = UserArtistSearchRequest.builder()
                .offset(offset)
                .limit(limit)
                .build();
        Page<UserArtistSearchResponse> data = userArtistService.searchUserArtist(request);

        List<UserArtistSearchResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "아티스트 thumbnail 임시 다운로드 URL 생성 및 이동",
            notes = "AWS S3에 업로드된 파일의 임시 다운로드 URL을 생성 후 이동한다."
    )
    @Secured({USER})
    @GetMapping("/{id}/thumbnail")
    public ResponseEntity<Object> thumbnailPreSignedUrl(
            @ApiParam(value = "유저 아티스트 ID")
            @PathVariable String id,
            @ApiParam(value = "만료 시간(초)")
            @RequestParam(required = false, defaultValue = "600") @Min(0) Long expSeconds
    ) throws URISyntaxException, IOException, InvalidKeySpecException {

        UserArtistSearchResponse userArtistSearchResponse = userArtistService.getUserArtist(id);

        if (userArtistSearchResponse.getThumbnailKey() == null) {
            throw new ResourceNotFoundException("Cannot find Thumbnail id=" + id);
        }

        PreSignedUrlResponse data =
                storageService.generatePreSignedUrl(userArtistSearchResponse.getThumbnailKey(), expSeconds);

        if (data == null) {
            throw new ResourceNotFoundException("Cannot find Thumbnail Storage id=" + id);
        }

        return ResponseHelper.newContentsSuccessRedirect(data.getUrl());
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "아티스트 logo 임시 다운로드 URL 생성 및 이동",
            notes = "AWS S3에 업로드된 파일의 임시 다운로드 URL을 생성 후 이동한다."
    )
    @Secured({USER})
    @GetMapping("/{id}/logo")
    public ResponseEntity<Object> logoPreSignedUrl(
            @ApiParam(value = "유저 아티스트 ID")
            @PathVariable String id,
            @ApiParam(value = "만료 시간(초)")
            @RequestParam(required = false, defaultValue = "600") @Min(0) Long expSeconds
    ) throws URISyntaxException, IOException, InvalidKeySpecException {

        UserArtistSearchResponse userArtistSearchResponse = userArtistService.getUserArtist(id);

        if (userArtistSearchResponse.getLogoKey() == null) {
            throw new ResourceNotFoundException("Cannot find logo id=" + id);
        }

        PreSignedUrlResponse data =
                storageService.generatePreSignedUrl(userArtistSearchResponse.getLogoKey(), expSeconds);

        if (data == null) {
            throw new ResourceNotFoundException("Cannot find logo Storage id=" + id);
        }

        return ResponseHelper.newContentsSuccessRedirect(data.getUrl());
    }

}
