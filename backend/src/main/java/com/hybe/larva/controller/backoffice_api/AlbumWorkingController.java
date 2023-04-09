package com.hybe.larva.controller.backoffice_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.album.*;
import com.hybe.larva.dto.album_working.*;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.enums.AlbumState;
import com.hybe.larva.exception.ProtectedResourceException;
import com.hybe.larva.exception.ResourceNotFoundException;
import com.hybe.larva.service.AlbumWorkingService;
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
import java.util.Arrays;
import java.util.List;

import static com.hybe.larva.enums.UserRole.ROLES.*;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Slf4j
@Api(tags = "AlbumWorking")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/albums/working")
@RestController
public class AlbumWorkingController {

    private final AlbumWorkingService albumWorkingService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "앨범 추가",
            notes = "앨범을 추가한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER})
    @PostMapping("")
    public SingleResult<AlbumWorkingResponse> addAlbum(
            @RequestBody @Valid AlbumWorkingAddRequest request
    ) {
        AlbumWorkingResponse data = albumWorkingService.addAlbumWorking(request);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "앨범 검색",
            notes = "선택한 조건으로 앨범을 검색한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER, VIEWER})
    @GetMapping("")
    public ListResult<AlbumWorkingSearchResponse> searchAlbum(
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
        Page<AlbumWorkingSearchResponse> data = albumWorkingService.searchAlbumWorking(request);

        List<AlbumWorkingSearchResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "앨범 조회",
            notes = "앨범을 조회한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER, VIEWER})
    @GetMapping("/{id}")
    public SingleResult<AlbumWorkingDetailResponse> getAlbum(
            @ApiParam(value = "앨범 ID")
            @PathVariable String id
    ) {

        AlbumWorkingDetailResponse data = albumWorkingService.getAlbumWorking(id);

        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "앨범내 카드 조회",
            notes = "앨범내 카드를 조회한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER, VIEWER})
    @GetMapping("/{id}/cards")
    public ListResult<CardResponse> getAlbumCard(
            @ApiParam(value = "앨범 ID")
            @PathVariable String id
    ) {
        List<CardResponse> data = albumWorkingService.getAlbumCard(id);

        return ResponseHelper.newListSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "앨범 수정",
            notes = "앨범을 수정한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER})
    @PatchMapping("/{id}")
    public SingleResult<AlbumWorkingDetailResponse> updateAlbum(
            @ApiParam(value = "앨범 ID")
            @PathVariable String id,
            @ApiParam(value = "업데이트 정보")
            @RequestBody @Valid AlbumUpdateRequest request
    ) {
        AlbumWorkingDetailResponse data = albumWorkingService.updateAlbumWorking(id, request);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "앨범 삭제",
            notes = "앨범을 삭제한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER})
    @DeleteMapping("/{id}")
    public CommonResult deleteAlbum(
            @ApiParam(value = "앨범 ID")
            @PathVariable String id
    ) {
        albumWorkingService.deleteAlbumWorking(id);
        return ResponseHelper.newSuccessResult();
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "카드 추가/수정/삭제",
            notes = "카드를 추가/수정/삭제한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER})
    @PostMapping("/{id}/cards")
    public SingleResult<AlbumWorkingDetailResponse> saveCard(
            @ApiParam(value = "앨범 ID")
            @PathVariable String id,
            @RequestBody @Valid List<CardSaveRequest> request
    ) {
        try {
            albumWorkingService.saveCard(id, request);
            AlbumWorkingDetailResponse data = albumWorkingService.getAlbumWorking(id);
            return ResponseHelper.newSingleSuccessResult(data);
        } catch (ResourceNotFoundException e2) {
            albumWorkingService.deleteCardSaveRequestContents(id, request);
            throw new ResourceNotFoundException(e2.getMessage());
        } catch (Exception e) {
            albumWorkingService.deleteCardSaveRequestContents(id, request);
            throw new ProtectedResourceException(e.getMessage());
        }
    }

    // 상태 update api 추가가
    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "앨범 상태 수정(Working)",
            notes = "앨범 상태를 수정(Working)한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER})
    @PatchMapping("/{id}/working")
    public SingleResult<AlbumWorkingDetailResponse> updateAlbumWorkingModifying(
            @ApiParam(value = "앨범 ID")
            @PathVariable String id
    ) {
        AlbumWorkingDetailResponse data =
                albumWorkingService.updateAlbumWorkingState(id, AlbumState.WORKING, Arrays.asList(AlbumState.SUBMITTED, AlbumState.REJECTED, AlbumState.DEPLOYED));
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "앨범 상태 수정(Submitted)",
            notes = "앨범 상태를 수정(Submitted)한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER})
    @PatchMapping("/{id}/submitted")
    public SingleResult<AlbumWorkingDetailResponse> updateAlbumWorkingSubmitted(
            @ApiParam(value = "앨범 ID")
            @PathVariable String id
    ) {
        AlbumWorkingDetailResponse data =
                albumWorkingService.updateAlbumWorkingState(id, AlbumState.SUBMITTED, Arrays.asList(AlbumState.WORKING, AlbumState.CONFIRMED));


        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "앨범 상태 수정(Rejected)",
            notes = "앨범 상태를 수정(Rejected)한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER})
    @PatchMapping("/{id}/rejected")
    public SingleResult<AlbumWorkingDetailResponse> updateAlbumWorkingRejected(
            @ApiParam(value = "앨범 ID")
            @PathVariable String id,
            @ApiParam(value = "업데이트 정보")
            @RequestBody @Valid AlbumWorkingRejectedRequest request
    ) {
        AlbumWorkingDetailResponse data = albumWorkingService.updateAlbumWorkingRejected(id, request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "앨범 상태 수정(Reviewing)",
            notes = "앨범 상태를 수정(Reviewing)한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER})
    @PatchMapping("/{id}/reviewing")
    public SingleResult<AlbumWorkingDetailResponse> updateAlbumWorkingReviewing(
            @ApiParam(value = "앨범 ID")
            @PathVariable String id
    ) {
        AlbumWorkingDetailResponse data = albumWorkingService.updateAlbumWorkingState(id, AlbumState.REVIEWING, Arrays.asList(AlbumState.SUBMITTED));
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "앨범 상태 수정(Confirmed)",
            notes = "앨범 상태를 수정(Confirmed)한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER})
    @PatchMapping("/{id}/confirmed")
    public SingleResult<AlbumWorkingDetailResponse> updateAlbumWorkingConfirmed(
            @ApiParam(value = "앨범 ID")
            @PathVariable String id
    ) {
        AlbumWorkingDetailResponse data = albumWorkingService.updateAlbumWorkingState(id, AlbumState.CONFIRMED, Arrays.asList(AlbumState.REVIEWING));
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "앨범 상태 이력 조회",
            notes = "선택한 조건으로 앨범 상태 이력을 조회한다."
    )
    @Secured({CONTENTS_EDITOR, CONTENTS_MANAGER})
    @GetMapping("/{id}/history")
    public ListResult<AlbumWorkingHistorySearchResponse> searchAlbumWorkingHistory(
            @ApiParam(value = "앨범 ID")
            @PathVariable String id,
            @ApiParam(value = "검색 시작일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime from,
            @ApiParam(value = "검색 종료일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime to,
            @ApiParam(value = "Offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @ApiParam(value = "version")
            @RequestParam(required = false) Integer version,
            @ApiParam(value = "state")
            @RequestParam(required = false) AlbumState state

    ) {
        AlbumWorkingHistorySearchRequest request = AlbumWorkingHistorySearchRequest.builder()
                .from(from).to(to).offset(offset).limit(limit)
                .albumId(id).version(version).state(state)
                .build();
        Page<AlbumWorkingHistorySearchResponse> data = albumWorkingService.searchAlbumWorkingHistory(request);

        List<AlbumWorkingHistorySearchResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }
}
