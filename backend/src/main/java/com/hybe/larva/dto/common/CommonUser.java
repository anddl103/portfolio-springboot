package com.hybe.larva.dto.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

@Slf4j
public class CommonUser {

    public static UserDetail userDetail() {

        UserDetail userDetail = UserDetail.builder().build();

        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                userDetail = (UserDetail) SecurityContextHolder.getContext().getAuthentication().getDetails();
            }
        } catch (ClassCastException e) {
            log.info("사용자 정보 확인");
        } catch (Exception e) {
            log.info("exception : " + e.getMessage());
        }
        return userDetail;
    }

    public static String getUid() {
        return userDetail().getUsername();
    }

    public static String getRole() { return userDetail().getRole(); }
}
