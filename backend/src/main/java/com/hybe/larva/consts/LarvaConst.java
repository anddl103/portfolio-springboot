package com.hybe.larva.consts;

public class LarvaConst {

    // Maintenance filename which is uploaded to Amazon S3
    public static final String MAINTENANCE_FILENAME = "larva.maintenance.json";

    // Access Token header
    public static final String X_ACCESS_TOKEN = "x-access-token";

    public static final String ADMIN_ROLE = "ADMIN_ROLE";

    // Fixed auth provider
    // FIXME: to enum
    public static final String PROVIDER_MOBEEK = "mobee-k";
    public static final String PROVIDER_HYBE = "hybe";

    // Token type
    public static final String ACCESS_TOKEN = "access";
    public static final String REFRESH_TOKEN = "refresh";

    // Token tag
    public static final String TAG_ADMIN = "admin";
    public static final String TAG_USER = "user";

    // Token valid hours
    public static final long ACCESS_TOKEN_VALID_HOURS = 12L;
    public static final long REFRESH_TOKEN_VALID_HOURS = 24L * 60;

    // default value
    public static final int DEFAULT_WALLPAPER_MAX_DOWNLOAD_COUNT = 2;

    // nfc parameter
    public static final String NFC_PARAM_AUTH = "auth";
    public static final String NFC_PARAM_SN = "sn";

    // fixed orderId for test accessory (delivery.typ = NONE)
    public static final String FIXED_ORDER_ID_TEST_ACCESSORY = "test_accessory_order";
    public static final String FIXED_SN_TEST_ACCESSORY = "eaf15002-5ca0-11eb-ae93-0242ac130002";
    // fixed orderId for free accessory delivered by default
    public static final String FIXED_ORDER_ID_DEFAULT_ACCESSORY = "default_accessory_order";
    // fixed orderId for free accessory delivered monthly
    public static final String FIXED_ORDER_ID_MONTHLY_ACCESSORY = "monthly_accessory_order";

    // swagger
    public static final String SWAGGER_TAG_ADMIN = "[ 어드민 API 참조 ]";
    public static final String SWAGGER_TAG_USER = "[ 유저 API 참조 ]";
    public static final String SWAGGER_TAG_TOOL = "[ 생산툴 API 참조 ]";
}
