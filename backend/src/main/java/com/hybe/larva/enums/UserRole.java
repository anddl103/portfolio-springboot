package com.hybe.larva.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum UserRole {

    ROLE_SUPER_ADMIN(ROLES.SUPER_ADMIN, "Super Administrator"),
    ROLE_VIEWER(ROLES.VIEWER, "Viewer"),
    ROLE_CONTENTS_EDITOR(ROLES.CONTENTS_EDITOR, "Contents Editor"),
    ROLE_CONTENTS_MANAGER(ROLES.CONTENTS_MANAGER, "Contents Manager"),
    ROLE_PRODUCT_MANAGER(ROLES.PRODUCT_MANAGER, "Product Manager"),
    ROLE_CS_MANAGER(ROLES.CS_MANAGER, "CS Manager"),
    ROLE_USER(ROLES.USER, "Normal user");

    public static class ROLES {
        public static final String SUPER_ADMIN = "ROLE_SUPER_ADMIN";
        public static final String VIEWER = "ROLE_VIEWER";
        public static final String CONTENTS_EDITOR = "ROLE_CONTENTS_EDITOR";
        public static final String CONTENTS_MANAGER = "ROLE_CONTENTS_MANAGER";
        public static final String PRODUCT_MANAGER = "ROLE_PRODUCT_MANAGER";
        public static final String CS_MANAGER = "ROLE_CS_MANAGER";
        public static final String USER = "ROLE_USER";
    }

    private final String code;
    private final String description;

}
