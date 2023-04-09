package com.hybe.larva.controller.backoffice_api;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserRecord;
import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.admin.AdminAddRequest;
import com.hybe.larva.dto.common.*;
import com.hybe.larva.dto.user_info.*;
import com.hybe.larva.exception.ResourceNotFoundException;
import com.hybe.larva.service.UserInfoService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hybe.larva.enums.UserRole.ROLES.*;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Slf4j
@Api(tags = "User")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/users")
@RestController
public class UserController {

    private final UserInfoService userInfoService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "유저 정보 조회",
            notes = "유저 정보를 조회한다."
    )
    @Secured({CS_MANAGER})
    @GetMapping("/{id}")
    public SingleResult<UserInfoResponse> getUserInfo(
            @ApiParam(value = "유저 id")
            @PathVariable String id
    ) {
        UserInfoResponse data = userInfoService.getUserInfo(id);
        UserRecord userRecord = null;
        try {
            userRecord = FirebaseAuth.getInstance().getUser(data.getUid());
        } catch (FirebaseAuthException e) {
        }
        if (userRecord != null) {
            List<String> providers = new ArrayList<>();
            for (UserInfo providerDatum : userRecord.getProviderData()) {
                providers.add(providerDatum.getProviderId());
            }
            data.setEmail(userRecord.getEmail());
            data.setProviders(providers);
        }

        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "유저 정보 조회",
            notes = "유저 정보를 조회한다."
    )
    @Secured({CS_MANAGER})
    @GetMapping("")
    public ListResult<UserInfoResponse> searchUserInfo(
            @ApiParam(value = "Offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit

    )  {
        UserInfoSearchRequest request = UserInfoSearchRequest.builder()
                .offset(offset)
                .limit(limit)
                .build();
        Page<UserInfoResponse> data = userInfoService.searchUserInfo(request);

        List<UserInfoResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        for (UserInfoResponse userInfoResponse : list) {
            if (Boolean.FALSE.equals(userInfoResponse.getWithdrawal().isFlag())) {
                UserRecord userRecord = null;
                try {
                    userRecord = FirebaseAuth.getInstance().getUser(userInfoResponse.getUid());
                } catch (FirebaseAuthException e) {
                }
                if (userRecord != null) {
                    List<String> providers = new ArrayList<>();
                    for (UserInfo providerDatum : userRecord.getProviderData()) {
                        providers.add(providerDatum.getProviderId());
                    }
                    userInfoResponse.setEmail(userRecord.getEmail());
                    userInfoResponse.setProviders(providers);
                }
            }
        }

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    // 회원 탈퇴 처리 후 삭제 예정
    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_USER},
            value = "이메일 중복 검사",
            notes = "이메일이 중복되는지 검사한다."
    )
    @GetMapping("/email/{email}/check")
    public SingleResult<DuplicationCheckResponse> checkUserEmail(
            @ApiParam(value = "이메일")
            @PathVariable String email
    )  {
        boolean duplicated = false;
        try {
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
            if (userRecord != null) {
                try {
                    UserInfoResponse userInfo = userInfoService.getUserInfoUid(userRecord.getUid());
                    if (userInfo != null) {
                        duplicated = true;
                    }

                } catch (ResourceNotFoundException e) {

                }
            }
        } catch (FirebaseAuthException e) {

        }
        DuplicationCheckResponse data = new DuplicationCheckResponse(duplicated);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "유저 카드 정보 조회",
            notes = "유저 카드 정보를 조회한다."
    )
    @Secured({CS_MANAGER})
    @GetMapping("/{id}/cards")
    public ListResult<UserInfoCardResponse> searchUserCard(
            @ApiParam(value = "유저 id")
            @PathVariable String id,
            @ApiParam(value = "page")
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @ApiParam(value = "pageSize")
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @ApiParam(value = "앨범 Id")
            @RequestParam(required = false) String albumId,
            @ApiParam(value = "아티스트 Id")
            @RequestParam(required = false) String artistId,
            @ApiParam(value = "favorite")
            @RequestParam(required = false) Boolean favorite,
            @ApiParam(value = "keyword, like 검색")
            @RequestParam(required = false) String keyword
    ) {
        UserInfoCardSearchRequest request = UserInfoCardSearchRequest.builder()
                .artistId(artistId)
                .favorite(favorite)
                .albumId(albumId)
                .keyword(keyword)
                .page(page)
                .pageSize(pageSize)
                .build();
        UserInfoResponse userInfo = userInfoService.getUserInfo(id);
        Page<UserInfoCardResponse> data = userInfoService.searchUserCard(request, userInfo.getUid());

        List<UserInfoCardResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "유저 앨범 정보 조회",
            notes = "유저 앨범 정보를 조회한다."
    )
    @Secured({CS_MANAGER})
    @GetMapping("/{id}/albums")
    public ListResult<UserInfoAlbumResponse> searchUserAlbum(
            @ApiParam(value = "유저 id")
            @PathVariable String id,
            @ApiParam(value = "page")
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @ApiParam(value = "pageSize")
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @ApiParam(value = "아티스트 Id")
            @RequestParam(required = false) String artistId,
            @ApiParam(value = "keyword, like 검색")
            @RequestParam(required = false) String keyword
    ) {
        UserInfoAlbumSearchRequest request = UserInfoAlbumSearchRequest.builder()
                .artistId(artistId)
                .keyword(keyword)
                .page(page)
                .pageSize(pageSize)
                .build();
        UserInfoResponse userInfo = userInfoService.getUserInfo(id);
        Page<UserInfoAlbumResponse> data = userInfoService.searchUserAlbum(request, userInfo.getUid());

        List<UserInfoAlbumResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "유저 아티스트 정보 조회",
            notes = "유저 아티스트 정보를 조회한다."
    )
    @Secured({CS_MANAGER})
    @GetMapping("/{id}/artists")
    public ListResult<UserInfoArtistResponse> searchUserArtist(
            @ApiParam(value = "유저 id")
            @PathVariable String id,
            @ApiParam(value = "page")
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @ApiParam(value = "pageSize")
            @RequestParam(required = false, defaultValue = "10") Integer pageSize
    ) {
        UserInfoArtistSearchRequest request = UserInfoArtistSearchRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .build();
        UserInfoResponse userInfo = userInfoService.getUserInfo(id);
        Page<UserInfoArtistResponse> data = userInfoService.searchUserArtist(request, userInfo.getUid());

        List<UserInfoArtistResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "유저 아티스트 정보 조회",
            notes = "유저 아티스트 정보를 조회한다."
    )
    @Secured({CS_MANAGER})
    @GetMapping("/{id}/productKeyHistory")
    public ListResult<UserInfoProductKeyHistoryResponse> searchUserProductKeyHistory(
            @ApiParam(value = "유저 id")
            @PathVariable String id,
            @ApiParam(value = "검색 시작일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime from,
            @ApiParam(value = "검색 종료일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime to,
            @ApiParam(value = "page")
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @ApiParam(value = "pageSize")
            @RequestParam(required = false, defaultValue = "10") Integer pageSize
    ) {
        UserInfoProductKeyHistorySearchRequest request = UserInfoProductKeyHistorySearchRequest.builder()
                .from(from)
                .to(to)
                .page(page)
                .pageSize(pageSize)
                .build();
        UserInfoResponse userInfo = userInfoService.getUserInfo(id);
        Page<UserInfoProductKeyHistoryResponse> data = userInfoService.searchUserProductKeyHistory(request, userInfo.getUid());

        List<UserInfoProductKeyHistoryResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }
    
    // 임시
    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "사용자 추가",
            notes = "사용자를 추가한다."
    )
    @Secured({SUPER_ADMIN})
    @PostMapping("")
    public CommonResult addUser(
            @RequestBody @Valid AdminAddRequest request
    ) throws FirebaseAuthException {

        UserRecord.CreateRequest fireBaseUser = new UserRecord.CreateRequest()
                .setEmail(request.getEmail())
                .setPassword(request.getPassword());

        UserRecord user = FirebaseAuth.getInstance().createUser(fireBaseUser);

        Map<String, Boolean> deviceTokens = new HashMap<>();

        UserInfoAddRequest userInfo = UserInfoAddRequest.builder()
                .deviceTokens(deviceTokens)
                .push(true)
                .build();

        userInfoService.addUserInfo(userInfo);

        return ResponseHelper.newSuccessResult();
    }

}
