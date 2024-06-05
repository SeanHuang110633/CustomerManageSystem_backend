package com.RM.manageSystem.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.Map;

public class JWTUtil {

    private static final String KEY = "1RMCustomSystem";

    /**
     * 根據傳入的聲明（claims），生成一個JWT令牌。
     *
     * @param claims 包含各種聲明信息的映射表（例如，用戶ID，用戶名等）
     * @return 返回一個經過簽名的JWT令牌字符串
     */
    public static String genToken(Map<String, Object> claims) {
        return JWT.create()
                // 添加自定義聲明，將傳入的claims映射表添加到令牌中
                .withClaim("claims", claims)
                // 設置令牌的過期時間，設定為當前時間之後的12小時
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 *12 ))
                // 使用HMAC256算法和定義的KEY來簽名令牌，生成最終的令牌字符串
                .sign(Algorithm.HMAC256(KEY));
    }

    /**
     * 驗證並解析JWT令牌，提取其中的聲明信息。
     *
     * @param token 待驗證和解析的JWT令牌字符串
     * @return 返回一個映射表，包含了從令牌中解析出的聲明信息
     */
    public static Map<String, Object> parseToken(String token) {
        // 使用相同的HMAC256算法和KEY來構建JWT驗證器
        return JWT.require(Algorithm.HMAC256(KEY)).build()
                // 驗證傳入的令牌，並返回一個解碼後的JWT物件
                .verify(token)
                // 從JWT物件中提取名為"claims"的聲明
                .getClaim("claims")
                // 將提取出的聲明轉為映射表格式
                .asMap();
    }
}

