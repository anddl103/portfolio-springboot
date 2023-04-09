package com.hybe.larva.controller.client_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.storage.PreSignedUrlResponse;
import com.hybe.larva.dto.view_user_card.ViewUserCardResponse;
import com.hybe.larva.dto.view_user_card.ViewUserCardSearchRequest;
import com.hybe.larva.exception.AuthException;
import com.hybe.larva.exception.ResourceNotFoundException;
import com.hybe.larva.service.StorageService;
import com.hybe.larva.service.UserAlbumService;
import com.hybe.larva.service.ViewUserCardService;
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
@Api(tags = "UserCard")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/userCards")
@RestController
public class UserCardController {

    private final ViewUserCardService viewUserCardService;
    private final UserAlbumService userAlbumService;
    private final StorageService storageService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "유저 카드 조회",
            notes = "유저 카드를 조회한다."
    )
    @Secured({USER})
    @GetMapping("/{id}")
    public SingleResult<ViewUserCardResponse> getUserCard(
            @ApiParam(value = "유저 카드 ID")
            @PathVariable String id
    ) {
        ViewUserCardResponse data = viewUserCardService.getUserCard(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "유저 카드 검색",
            notes = "선택한 조건으로 유저 카드를 검색한다."
    )
    @Secured({USER})
    @GetMapping("")
    public ListResult<ViewUserCardResponse> searchUserCard(
            @ApiParam(value = "offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @ApiParam(value = "아티스트 Id")
            @RequestParam(required = false) String artistId,
            @ApiParam(value = "favorite")
            @RequestParam(required = false) Boolean favorite
    ) {
        ViewUserCardSearchRequest request = ViewUserCardSearchRequest.builder()
                .artistId(artistId)
                .favorite(favorite)
                .offset(offset)
                .limit(limit)
                .build();
        log.info("searc start");
        Page<ViewUserCardResponse> data = viewUserCardService.searchUserCard(request);
        log.info("searc end");

        List<ViewUserCardResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "유저 카드 favorite 설정",
            notes = "유저 카드의 favorite를 설정한다."
    )
    @Secured({USER})
    @PatchMapping("/{id}/favorite")
    public SingleResult<ViewUserCardResponse> updateUserCardFavorite(
            @ApiParam(value = "유저 카드 ID")
            @PathVariable String id,
            @ApiParam(value = "업데이트 정보")
            @RequestParam Boolean favorite
    ) {

        ViewUserCardResponse data = viewUserCardService.updateUserCardFavorite(id, favorite);

        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "Card Contents 임시 다운로드 URL 생성 및 이동",
            notes = "AWS S3에 업로드된 파일의 임시 다운로드 URL을 생성 후 이동한다."
    )
    @Secured({USER})
    @GetMapping("/{id}/contents")
    public ResponseEntity<Object>  preSignedUrl(
            @ApiParam(value = "유저 카드 ID")
            @PathVariable String id,
            @ApiParam(value = "키")
            @RequestParam String key,
            @ApiParam(value = "만료 시간(초)")
            @RequestParam(required = false, defaultValue = "60") @Min(0) Long expSeconds
    ) throws URISyntaxException, IOException, InvalidKeySpecException {

        PreSignedUrlResponse data = null;
        ViewUserCardResponse viewUserCardResponse = viewUserCardService.getUserCard(id);
        if (viewUserCardResponse.getContents().getImageKey() == null &&
                viewUserCardResponse.getContents().getVideoKey() == null) {
            throw new ResourceNotFoundException("Cannot find Contents id=" + id);
        }

        if (viewUserCardResponse.getContents().getImageKey() != null &&
                viewUserCardResponse.getContents().getImageKey().equals(key)) {
            data = storageService.generatePreSignedUrl(key, expSeconds);
        }

        if (viewUserCardResponse.getContents().getVideoKey() != null &&
                viewUserCardResponse.getContents().getVideoKey().equals(key)) {
            data = storageService.generatePreSignedUrl(key, expSeconds);
        }

        if (data == null) {
            throw new AuthException("no permission");
        }

        return ResponseHelper.newContentsSuccessRedirect(data.getUrl());
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "Card thumbnail 임시 다운로드 URL 생성 및 이동",
            notes = "AWS S3에 업로드된 파일의 임시 다운로드 URL을 생성 후 이동한다."
    )
    @Secured({USER})
    @GetMapping("/{id}/thumbnail")
    public ResponseEntity<Object> thumbnailPreSignedUrl(
            @ApiParam(value = "유저 카드 ID")
            @PathVariable String id,
            @ApiParam(value = "collected")
            @RequestParam(required = false, defaultValue = "false") boolean isCollected,
            @ApiParam(value = "만료 시간(초)")
            @RequestParam(required = false, defaultValue = "600") @Min(0) Long expSeconds
    ) throws URISyntaxException, IOException, InvalidKeySpecException {

        ViewUserCardResponse viewUserCardResponse = viewUserCardService.getUserCard(id, isCollected);

        if (viewUserCardResponse.getContents().getThumbnailKey() == null) {
            throw new ResourceNotFoundException("Cannot find Thumbnail id=" + id);
        }

        PreSignedUrlResponse data =
                storageService.generatePreSignedUrl(viewUserCardResponse.getContents().getThumbnailKey(), expSeconds);

        if (data == null) {
            throw new ResourceNotFoundException("Cannot find Thumbnail Storage id=" + id);
        }

        return ResponseHelper.newContentsSuccessRedirect(data.getUrl());
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "Reward Contents Image 임시 다운로드 URL 생성 및 이동",
            notes = "AWS S3에 업로드된 파일의 임시 다운로드 URL을 생성 후 이동한다."
    )
    @Secured({USER})
    @GetMapping("/{id}/contents/image")
    public ResponseEntity<Object> contentsImagePreSignedUrl(
            @ApiParam(value = "유저 카드 ID")
            @PathVariable String id,
            @ApiParam(value = "만료 시간(초)")
            @RequestParam(required = false, defaultValue = "600") @Min(0) Long expSeconds
    ) throws URISyntaxException, IOException, InvalidKeySpecException {

        ViewUserCardResponse viewUserCardResponse = viewUserCardService.getUserCard(id);

        if (viewUserCardResponse.getContents().getImageKey() == null) {
            throw new ResourceNotFoundException("Cannot find Contents Image id=" + id);
        }

        PreSignedUrlResponse data =
                storageService.generatePreSignedUrl(viewUserCardResponse.getContents().getImageKey(), expSeconds);

        if (data == null) {
            throw new ResourceNotFoundException("Cannot find Contents Image Storage id=" + id);
        }

        return ResponseHelper.newContentsSuccessRedirect(data.getUrl());
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "Reward Contents Video 임시 다운로드 URL 생성 및 이동",
            notes = "AWS S3에 업로드된 파일의 임시 다운로드 URL을 생성 후 이동한다."
    )
    @Secured({USER})
    @GetMapping("/{id}/contents/video")
    public ResponseEntity<Object> contentsVideoPreSignedUrl(
            @ApiParam(value = "유저 카드 ID")
            @PathVariable String id,
            @ApiParam(value = "만료 시간(초)")
            @RequestParam(required = false, defaultValue = "3600") @Min(0) Long expSeconds
    ) throws URISyntaxException, IOException, InvalidKeySpecException {

        ViewUserCardResponse viewUserCardResponse = viewUserCardService.getUserCard(id);

        if (viewUserCardResponse.getContents().getVideoKey() == null) {
            throw new ResourceNotFoundException("Cannot find Contents Video id=" + id);
        }

        PreSignedUrlResponse data =
                storageService.generatePreSignedUrl(viewUserCardResponse.getContents().getVideoKey(), expSeconds);

        if (data == null) {
            throw new ResourceNotFoundException("Cannot find Reward Contents Video Storage id=" + id);
        }

        return ResponseHelper.newContentsSuccessRedirect(data.getUrl());
    }

}
