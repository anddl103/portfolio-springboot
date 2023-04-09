package com.hybe.larva.service;

import com.hybe.larva.entity.user_info.UserInfoRepoExt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommonService {

    private final UserInfoRepoExt userInfoRepoExt;

}
