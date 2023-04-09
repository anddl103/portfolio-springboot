package com.hybe.larva.controller.backoffice_api;

import com.google.firebase.auth.*;
import com.hybe.larva.consts.LarvaConst;
import com.hybe.larva.dto.admin.AdminAddRequest;
import com.hybe.larva.dto.admin.AdminResponse;
import com.hybe.larva.dto.admin.AdminUpdateRequest;
import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ListResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.dto.common.SingleResult;
import com.hybe.larva.enums.UserRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.hybe.larva.enums.UserRole.ROLES.*;

@Slf4j
@Api(tags = "Admin")
@RequiredArgsConstructor
@RequestMapping("${larva.api}/admins")
@RestController
public class AdminController {

//    private final UserInfoService userInfoService;

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "관리자 추가",
            notes = "관리자를 추가한다."
    )
    @Secured({SUPER_ADMIN})
    @PostMapping("")
    public CommonResult addAdmin(
            @RequestBody @Valid AdminAddRequest request
    ) throws FirebaseAuthException {

        UserRecord.CreateRequest fireBaseUser = new UserRecord.CreateRequest()
                .setEmail(request.getEmail())
                .setPassword(request.getPassword());

        UserRecord user= FirebaseAuth.getInstance().createUser(fireBaseUser);

//        if (user.isEmailVerified()) {
            Map<String, Object> claims = new HashMap<>();
            claims.put(LarvaConst.ADMIN_ROLE, request.getRole());
            FirebaseAuth.getInstance().setCustomUserClaimsAsync(user.getUid(), claims);
//        }

//        UserInfoResponse userInfoResponse =
//                userInfoService.addUserInfo(UserInfoAddRequest.builder().userUid(user.getUid()).build());

        return ResponseHelper.newSuccessResult();
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "관리자 수정",
            notes = "관리자를 추가한다."
    )
    @Secured({SUPER_ADMIN})
    @PatchMapping("")
    public CommonResult updateAdmin(
            @RequestBody @Valid AdminUpdateRequest request
    ) throws FirebaseAuthException {


        UserRecord.UpdateRequest fireBaseUser = new UserRecord.UpdateRequest(request.getUid());

        String uid = request.getUid();

        if (request.getPassword() != null) {
            fireBaseUser.setPassword(request.getPassword());
            FirebaseAuth.getInstance().updateUser(fireBaseUser);
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put(LarvaConst.ADMIN_ROLE, request.getRole());
        FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);


        return ResponseHelper.newSuccessResult();
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "관리자 목록",
            notes = "관리자 목록"
    )
    @Secured({SUPER_ADMIN, VIEWER})
    @GetMapping("")
    public ListResult<AdminResponse> searchAdmin(
    ) throws FirebaseAuthException, ExecutionException, InterruptedException {

//      ListUsersPage page = FirebaseAuth.getInstance().listUsers(null);
//      while (page != null) {
//          for (ExportedUserRecord user : page.getValues()) {
//                log.info("User: " + user.getUid());
//          }
//          page = page.getNextPage();
//      }

//      Iterate through all users. This will still retrieve users in batches,
//      buffering no more than 1000 users in memory at a time.
        ListUsersPage page = FirebaseAuth.getInstance().listUsers(null);

        List<AdminResponse> adminList = new ArrayList<>();

        List<UserRole> codes = adminRoleList();

        for (ExportedUserRecord user : page.iterateAll()) {
            for (int i=0; i < codes.size(); i++) {
                String code = codes.get(i).getCode();
                if (user.getCustomClaims().containsKey(LarvaConst.ADMIN_ROLE)) {
                    if (user.getCustomClaims().get(LarvaConst.ADMIN_ROLE).equals(code)) {
                        adminList.add(AdminResponse.builder()
                            .uid(user.getUid())
                            .email(user.getEmail())
                            .role(code)
                            .createdAt(user.getUserMetadata().getCreationTimestamp())
                            .lastSignInTime(user.getUserMetadata().getLastSignInTimestamp())
                            .build());
                    }
                }
            }
            // log.info("Verified : " + user.isEmailVerified());
        }

        return ResponseHelper.newListSuccessResult(adminList);
    }


    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "관리자 조회",
            notes = "관리자를 조회한다."
    )
    @Secured({SUPER_ADMIN, CONTENTS_MANAGER, CONTENTS_EDITOR, PRODUCT_MANAGER, VIEWER, CS_MANAGER})
    @GetMapping("/{id}")
    public SingleResult<AdminResponse> getAdmin(
            @ApiParam(value = "관리자 UID")
            @PathVariable String id
    ) throws FirebaseAuthException {

        AdminResponse data = null;

        UserRecord userRecord = FirebaseAuth.getInstance().getUser(id);

        List<UserRole> codes = adminRoleList();

        for (int i=0; i < codes.size(); i++) {
            String code = codes.get(i).getCode();

            if (userRecord.getCustomClaims().containsKey(LarvaConst.ADMIN_ROLE)) {
                if (userRecord.getCustomClaims().get(LarvaConst.ADMIN_ROLE).equals(code)) {
                    data = AdminResponse.builder()
                            .uid(userRecord.getUid())
                            .email(userRecord.getEmail())
                            .role(code)
                            .build();
                }
            }
        }

        return ResponseHelper.newSingleSuccessResult(data);
    }

    @ApiOperation(
            tags = {LarvaConst.SWAGGER_TAG_ADMIN},
            value = "관리자 삭제",
            notes = "관리자를 삭제한다."
    )
    @Secured({SUPER_ADMIN})
    @DeleteMapping("/{id}")
    public CommonResult deleteNfcCard(
            @ApiParam(value = "관리자 UID")
            @PathVariable String id
    ) throws FirebaseAuthException {
        FirebaseAuth.getInstance().deleteUser(id);
        return ResponseHelper.newSuccessResult();
    }


    private List<UserRole> adminRoleList() {

        return Arrays.asList(UserRole.values())
                .stream()
                .filter(a -> a != UserRole.ROLE_USER)
                .collect(Collectors.toList());
    }

}
