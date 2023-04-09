package com.hybe.larva.controller.backoffice_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.nfc_card.*;
import com.hybe.larva.service.NfcCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static com.hybe.larva.enums.UserRole.ROLES.*;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Api(tags = "NfcCard")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/nfcCards")
@RestController
public class NfcCardController {

    private final NfcCardService nfcCardService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "NFC 카드 추가",
            notes = "NFC 카드를 추가한다."
    )
    @Secured({PRODUCT_MANAGER})
    @PostMapping("")
    public SingleResult<NfcCardDetailResponse> addNfcCard(
            @RequestBody @Valid NfcCardAddRequest request
    ) {
        NfcCardDetailResponse data = nfcCardService.addNfcCard(request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "NFC 카드 검색",
            notes = "선택한 조건으로 NFC 카드를 검색한다."
    )
    @Secured({PRODUCT_MANAGER, VIEWER})
    @GetMapping("")
    public ListResult<NfcCardSearchResponse> searchNfcCard(
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
            @ApiParam(value = "앨범 ID 검색")
            @RequestParam(required = false) String albumId,
            @ApiParam(value = "아티스트 ID 검색")
            @RequestParam(required = false) String artistId

    ) {
        NfcCardSearchRequest request = NfcCardSearchRequest.builder()
                .from(from).to(to).offset(offset).limit(limit)
                .albumId(albumId)
                .keyword(keyword)
                .artistId(artistId)
                .build();
        Page<NfcCardSearchResponse> data = nfcCardService.searchNfcCard(request);

        List<NfcCardSearchResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "NFC 카드 조회",
            notes = "NFC 카드를 조회한다."
    )
    @Secured({PRODUCT_MANAGER, VIEWER})
    @GetMapping("/{id}")
    public SingleResult<NfcCardDetailResponse> getNfcCard(
            @ApiParam(value = "NFC 카드 ID")
            @PathVariable String id
    ) {
        NfcCardDetailResponse data = nfcCardService.getNfcCard(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "NFC 카드 수정",
            notes = "NFC 카드를 수정한다."
    )
    @Secured({PRODUCT_MANAGER})
    @PatchMapping("/{id}")
    public SingleResult<NfcCardDetailResponse> updateNfcCard(
            @ApiParam(value = "NFC 카드 ID")
            @PathVariable String id,
            @ApiParam(value = "업데이트 정보")
            @RequestBody @Valid NfcCardUpdateRequest request
    ) {
        NfcCardDetailResponse data = nfcCardService.updateNfcCard(id, request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "NFC 카드 삭제",
            notes = "NFC 카드를 삭제한다."
    )
    @Secured({PRODUCT_MANAGER})
    @DeleteMapping("/{id}")
    public CommonResult deleteNfcCard(
            @ApiParam(value = "NFC 카드 ID")
            @PathVariable String id
    ) {
        nfcCardService.deleteNfcCard(id);
        return ResponseHelper.newSuccessResult();
    }
}
