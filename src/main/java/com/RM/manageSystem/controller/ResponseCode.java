package com.RM.manageSystem.controller;

public class ResponseCode {
    /**
     * 成功狀態碼
     */
    public static final Integer SUCCESS = 20010;

    /**
     * 錯誤狀態碼
     */
    public static final Integer USER_NOT_FOUND = 40011;
    public static final Integer CUSTOMER_NOT_FOUND = 40021;
    public static final Integer INVALID_INPUT = 40031;
    public static final Integer VALIDATION_ERROR = 40031;     // 驗證錯誤
    public static final Integer TYPE_CONVERSION_ERROR = 50011; // 類型轉換錯誤
    public static final Integer TOKEN_EXPIRED = 50021;       // JWT token失效
    public static final Integer DATABASE_ERR = 50031;       // 資料庫異常
    public static final Integer SYSTEM_ERR = 50041;        // 其他系統異常
}
