package com.hybe.larva.controller.backoffice_api;

import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.dto.policy.PolicyAddRequest;
import com.hybe.larva.dto.policy.PolicyResponse;
import com.hybe.larva.dto.policy.PolicySearchRequest;
import com.hybe.larva.dto.policy.PolicyUpdateRequest;
import com.hybe.larva.enums.Usage;
import com.hybe.larva.service.PolicyService;
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

@Api(tags = "Policy")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/policies")
@RestController
public class PolicyController {

    private final PolicyService policyService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "이용 약관 추가",
            notes = "이용 약관을 추가한다."
    )
    @Secured({CS_MANAGER})
    @PostMapping("")
    public SingleResult<PolicyResponse> addPolicy(
            @RequestBody @Valid PolicyAddRequest request
    ) {

        PolicyResponse data = policyService.addPolicy(request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "이용 약관 검색",
            notes = "선택한 조건으로 이용 약관을 검색한다."
    )
    @Secured({CS_MANAGER, VIEWER})
    @GetMapping("")
    public ListResult<PolicyResponse> searchPolicy(
            @ApiParam(value = "검색 시작일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime from,
            @ApiParam(value = "검색 종료일시: UTC 기준 (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime to,
            @ApiParam(value = "Offset")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @ApiParam(value = "limit")
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @ApiParam(value = "이름, like 검색")
            @RequestParam(required = false) String keyword
    ) {
        PolicySearchRequest request = PolicySearchRequest.builder()
                .from(from).to(to).offset(offset).limit(limit)
                .build();
        Page<PolicyResponse> data = policyService.searchPolicy(request);

        List<PolicyResponse> list = data.getContent();
        ListResult.PageInfo pageInfo = ListResult.PageInfo.of(data);

        return ResponseHelper.newListSuccessResult(list, pageInfo);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "이용 약관 조회",
            notes = "이용 약관을 조회한다."
    )
    @Secured({CS_MANAGER, VIEWER})
    @GetMapping("/{id}")
    public SingleResult<PolicyResponse> getPolicy(
            @ApiParam(value = "이용 약관 ID")
            @PathVariable String id
    ) {
        PolicyResponse data = policyService.getPolicy(id);
        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "이용 약관 수정",
            notes = "이용 약관을 수정한다."
    )
    @Secured({CS_MANAGER})
    @PatchMapping("/{id}")
    public SingleResult<PolicyResponse> updatePolicy(
            @ApiParam(value = "이용 약관 ID")
            @PathVariable String id,
            @ApiParam(value = "업데이트 정보")
            @RequestBody @Valid PolicyUpdateRequest request
    ) {
        PolicyResponse data = policyService.updatePolicy(id, request);
        return ResponseHelper.newSingleSuccessResult(data);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "이용 약관 삭제",
            notes = "이용 약관을 삭제한다."
    )
    @Secured({CS_MANAGER})
    @DeleteMapping("/{id}")
    public CommonResult deletePolicy(
            @ApiParam(value = "이용 약관 ID")
            @PathVariable String id
    ) {
        policyService.deletePolicy(id);
        return ResponseHelper.newSuccessResult();
    }
}
