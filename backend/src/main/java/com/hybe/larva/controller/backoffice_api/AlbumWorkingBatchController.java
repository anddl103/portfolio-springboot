package com.hybe.larva.controller.backoffice_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.album.AlbumSearchRequest;
import com.hybe.larva.dto.album_working.*;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.enums.AlbumState;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.hybe.larva.enums.UserRole.ROLES.*;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Slf4j
@Api(tags = "AlbumWorkingBatch")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/batch")
@RestController
public class AlbumWorkingBatchController {

    private final AlbumWorkingService albumWorkingService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "앨범 검색",
            notes = "선택한 조건으로 앨범을 검색한다."
    )
    @Secured({CONTENTS_MANAGER, CONTENTS_EDITOR, VIEWER})
    @GetMapping("")
    public ListResult<AlbumWorkingBatchSearchResponse> searchAlbum(
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
                .state(Arrays.asList(AlbumState.CONFIRMED, AlbumState.DEPLOYING))
                .build();
        Page<AlbumWorkingBatchSearchResponse> data = albumWorkingService.searchAlbumWorkingBatch(request);

        List<AlbumWorkingBatchSearchResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "배치 대상 앨범 조회",
            notes = "배치 대상 앨범을 조회한다."
    )
    @Secured({CONTENTS_MANAGER, CONTENTS_EDITOR, VIEWER})
    @GetMapping("/{id}")
    public SingleResult<AlbumWorkingDetailResponse> getBatchJobItem(
            @ApiParam(value = "배치 대상 앨범 ID")
            @PathVariable String id
    ) {

        AlbumWorkingDetailResponse data = albumWorkingService.getAlbumWorking(id);

        return ResponseHelper.newSingleSuccessResult(data);
    }

    // 상태 update api 추가가
    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "배치 실행 여부를 수정",
            notes = "배치 실행 여부를 수정한다."
    )
    @Secured({CONTENTS_MANAGER})
    @PatchMapping("/jobEnabled")
    public SingleResult<BatchJobEnabledResponse> updateAlbumStateBatchJobEnabled(
            @ApiParam(value = "배치 실행 여부")
            @RequestBody BatchJobEnabledUpdateRequest request
    ) {
        BatchJobEnabledResponse data = albumWorkingService.updateAlbumWorkingJobEnabled(request);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "배치 상태 조회",
            notes = "배치 상태를 조회한다."
    )
    @Secured({CONTENTS_MANAGER, CONTENTS_EDITOR, VIEWER})
    @GetMapping("/jobEnabled")
    public SingleResult<BatchJobEnabledResponse> getAlbumStateBatchJobEnabled() {

        BatchJobEnabledResponse data = albumWorkingService.getAlbumWorkingJobEnabled();
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "배치 실행",
            notes = "배치를 실행한다."
    )
    @Secured({CONTENTS_MANAGER})
    @PostMapping("/execute")
    public CommonResult getAlbumStateBatchExecute() {

        albumWorkingService.getAlbumStateBatchExecute();
        return ResponseHelper.newSuccessResult();
    }

}
