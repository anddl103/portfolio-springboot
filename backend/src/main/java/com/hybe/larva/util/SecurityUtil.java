package com.hybe.larva.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal().toString();
    }

//    public static boolean isAdmin() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return authentication.getAuthorities().stream().anyMatch(authority ->
//                AdminRole.FULL_DIRECTOR.equals(authority.getAuthority()));
//    }
//
//    public static boolean isClient() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return authentication.getAuthorities().stream().anyMatch(authority ->
//                AdminRole.CONTENTS_MANAGER.equals(authority.getAuthority()));
//    }
}
