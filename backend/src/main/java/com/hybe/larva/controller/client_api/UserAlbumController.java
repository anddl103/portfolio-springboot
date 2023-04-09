package com.hybe.larva.controller.client_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.storage.PreSignedUrlResponse;
import com.hybe.larva.dto.user_album.*;
import com.hybe.larva.exception.AuthException;
import com.hybe.larva.exception.ResourceNotFoundException;
import com.hybe.larva.service.StorageService;
import com.hybe.larva.service.UserAlbumService;
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

import static com.hybe.larva.enums.UserRole.ROLES.USER;

@Slf4j
@Api(tags = "UserAlbum")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/userAlbums")
@RestController
public class UserAlbumController {

    private final UserAlbumService userAlbumService;
    private final StorageService storageService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "유저 앨범 검색",
            notes = "선택한 조건으로 유저 앨범를 검색한다."
    )
    @Secured({USER})
    @GetMapping("")
    public ListResult<UserAlbumSearchResponse> searchUserCard(
            @ApiParam(value = "offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @ApiParam(value = "아티스트 Id")
            @RequestParam(required = false) String artistId
    ) {
        UserAlbumSearchRequest request = UserAlbumSearchRequest.builder()
                .artistId(artistId)
                .offset(offset)
                .limit(limit)
                .build();
        Page<UserAlbumSearchResponse> data = userAlbumService.searchUserAlbum(request);

        List<UserAlbumSearchResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "유저 앨범 조회",
            notes = "유저 앨범을 조회한다."
    )
    @Secured({USER})
    @GetMapping("/{id}")
    public SingleResult<UserAlbumSearchResponse> getUserAlbum(
            @ApiParam(value = "유저 앨범 ID")
            @PathVariable String id
    ) {
        UserAlbumSearchResponse data = userAlbumService.getUserAlbum(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "유저 앨범의 카드 조회",
            notes = "유저 앨범의 카드를 조회한다."
    )
    @Secured({USER})
    @GetMapping("/{id}/cards")
    public ListResult<UserAlbumCardResponse> getUserAlbumCard(
            @ApiParam(value = "offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "-1") Integer limit,
            @ApiParam(value = "유저 앨범 ID")
            @PathVariable String id
    ) {
        UserAlbumCardSearchRequest request = UserAlbumCardSearchRequest.builder()
                .offset(0)
                .limit(-1)
                .userAlbumId(id)
                .build();
        Page<UserAlbumCardResponse> data = userAlbumService.getUserAlbumCard(request);
        List<UserAlbumCardResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "유저 앨범의 보상 조회",
            notes = "유저 앨범의 보상를 조회한다."
    )
    @Secured({USER})
    @GetMapping("/{id}/reward")
    public SingleResult<UserAlbumRewardResponse> getUserAlbumReward(
            @ApiParam(value = "유저 앨범 ID")
            @PathVariable String id
    ) {
        UserAlbumRewardResponse data = userAlbumService.getUserAlbumReward(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    // 삭제 예정
    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "Reward Contents 임시 다운로드 URL 생성 및 이동",
            notes = "AWS S3에 업로드된 파일의 임시 다운로드 URL을 생성 후 이동한다."
    )
    @Secured({USER})
    @GetMapping("/{id}/reward/contents")
    public ResponseEntity<Object> rewardContentsPreSignedUrl(
            @ApiParam(value = "유저 앨범 ID")
            @PathVariable String id,
            @ApiParam(value = "키")
            @RequestParam String key,
            @ApiParam(value = "만료 시간(초)")
            @RequestParam(required = false, defaultValue = "60") @Min(0) Long expSeconds
    ) throws URISyntaxException, IOException, InvalidKeySpecException {
        PreSignedUrlResponse data = null;
        // complete 확인 추가 service 에서 확인
        UserAlbumRewardResponse userAlbumRewardResponse = userAlbumService.getUserAlbumReward(id);

        if (userAlbumRewardResponse.getImageKey() == null && userAlbumRewardResponse.getVideoKey() == null) {
            throw new ResourceNotFoundException("Cannot find Reward contents id=" + id);
        }

        if (userAlbumRewardResponse.getImageKey() != null && userAlbumRewardResponse.getImageKey().equals(key)) {
            data = storageService.generatePreSignedUrl(key, expSeconds);
        }

        if (userAlbumRewardResponse.getVideoKey() != null && userAlbumRewardResponse.getVideoKey().equals(key)) {
            data = storageService.generatePreSignedUrl(key, expSeconds);
        }

        if (data == null) {
            throw new AuthException("no permission");
        }

        return ResponseHelper.newContentsSuccessRedirect(data.getUrl());
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "Reward thumbnail 임시 다운로드 URL 생성 및 이동",
            notes = "AWS S3에 업로드된 파일의 임시 다운로드 URL을 생성 후 이동한다."
    )
    @Secured({USER})
    @GetMapping("/{id}/reward/thumbnail")
    public ResponseEntity<Object> rewardThumbnailPreSignedUrl(
            @ApiParam(value = "유저 앨범 ID")
            @PathVariable String id,
            @ApiParam(value = "만료 시간(초)")
            @RequestParam(required = false, defaultValue = "600") @Min(0) Long expSeconds
    ) throws URISyntaxException, IOException, InvalidKeySpecException {

        UserAlbumRewardResponse userAlbumRewardResponse = userAlbumService.getUserAlbumReward(id);

        if (userAlbumRewardResponse.getThumbnailKey() == null) {
            throw new ResourceNotFoundException("Cannot find Reward Thumbnail id=" + id);
        }

        PreSignedUrlResponse data =
                storageService.generatePreSignedUrl(userAlbumRewardResponse.getThumbnailKey(), expSeconds);

        if (data == null) {
            throw new ResourceNotFoundException("Cannot find Reward Thumbnail Storage id=" + id);
        }

        return ResponseHelper.newContentsSuccessRedirect(data.getUrl());
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "Reward Contents Image 임시 다운로드 URL 생성 및 이동",
            notes = "AWS S3에 업로드된 파일의 임시 다운로드 URL을 생성 후 이동한다."
    )
    @Secured({USER})
    @GetMapping("/{id}/reward/contents/image")
    public ResponseEntity<Object> rewardImagePreSignedUrl(
            @ApiParam(value = "유저 앨범 ID")
            @PathVariable String id,
            @ApiParam(value = "만료 시간(초)")
            @RequestParam(required = false, defaultValue = "600") @Min(0) Long expSeconds
    ) throws URISyntaxException, IOException, InvalidKeySpecException {

        UserAlbumRewardResponse userAlbumRewardResponse = userAlbumService.getUserAlbumReward(id);

        if (userAlbumRewardResponse.getImageKey() == null) {
            throw new ResourceNotFoundException("Cannot find Reward Contents Image id=" + id);
        }

        PreSignedUrlResponse data = storageService.generatePreSignedUrl(userAlbumRewardResponse.getImageKey(), expSeconds);

        if (data == null) {
            throw new ResourceNotFoundException("Cannot find Reward Contents Image Storage id=" + id);
        }

        return ResponseHelper.newContentsSuccessRedirect(data.getUrl());
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "Reward Contents Video 임시 다운로드 URL 생성 및 이동",
            notes = "AWS S3에 업로드된 파일의 임시 다운로드 URL을 생성 후 이동한다."
    )
    @Secured({USER})
    @GetMapping("/{id}/reward/contents/video")
    public ResponseEntity<Object> rewardVideoPreSignedUrl(
            @ApiParam(value = "유저 앨범 ID")
            @PathVariable String id,
            @ApiParam(value = "만료 시간(초)")
            @RequestParam(required = false, defaultValue = "3600") @Min(0) Long expSeconds
    ) throws URISyntaxException, IOException, InvalidKeySpecException {

        UserAlbumRewardResponse userAlbumRewardResponse = userAlbumService.getUserAlbumReward(id);

        if (userAlbumRewardResponse.getVideoKey() == null) {
            throw new ResourceNotFoundException("Cannot find Reward Contents Video id=" + id);
        }

        PreSignedUrlResponse data = storageService.generatePreSignedUrl(userAlbumRewardResponse.getVideoKey(), expSeconds);

        if (data == null) {
            throw new ResourceNotFoundException("Cannot find Reward Contents Video Storage id=" + id);
        }

        return ResponseHelper.newContentsSuccessRedirect(data.getUrl());
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "앨범 thumbnail 임시 다운로드 URL 생성 및 이동",
            notes = "AWS S3에 업로드된 파일의 임시 다운로드 URL을 생성 후 이동한다."
    )
    @Secured({USER})
    @GetMapping("/{id}/thumbnail")
    public ResponseEntity<Object> thumbnailPreSignedUrl(
            @ApiParam(value = "유저 앨범 ID")
            @PathVariable String id,
            @ApiParam(value = "만료 시간(초)")
            @RequestParam(required = false, defaultValue = "600") @Min(0) Long expSeconds
    ) throws URISyntaxException, IOException, InvalidKeySpecException {

        UserAlbumSearchResponse userAlbumSearchResponse = userAlbumService.getUserAlbum(id);

        if (userAlbumSearchResponse.getThumbnailKey() == null) {
            throw new ResourceNotFoundException("Cannot find Thumbnail id=" + id);
        }

        PreSignedUrlResponse data = storageService.generatePreSignedUrl(userAlbumSearchResponse.getThumbnailKey(), expSeconds);

        if (data == null) {
            throw new ResourceNotFoundException("Cannot find Thumbnail Storage id=" + id);
        }

        return ResponseHelper.newContentsSuccessRedirect(data.getUrl());
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "유저 보상  받기 ",
            notes = "유저 앨범의 보상을 받는다."
    )
    @Secured({USER})
    @PostMapping("/{id}/rewarded")
    public SingleResult<UserAlbumRewardResponse> updateUserAlbumRewarded(
            @ApiParam(value = "유저 앨범 ID")
            @PathVariable String id
    ) {
        UserAlbumRewardResponse data = userAlbumService.updateUserAlbumRewarded(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }
}
