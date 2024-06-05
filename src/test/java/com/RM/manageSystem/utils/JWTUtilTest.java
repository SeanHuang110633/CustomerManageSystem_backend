package com.RM.manageSystem.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JWTUtilTest {
    private Map<String, Object> claims;

    @BeforeEach
    public void setUp() {
        claims = new HashMap<>();
        claims.put("userId", "12345");
        claims.put("username", "testUser");
    }

    @Test
    public void testGenToken() {
        String token = JWTUtil.genToken(claims);
        assertNotNull(token, "Token should not be null");
    }

    @Test
    public void testParseToken() {
        String token = JWTUtil.genToken(claims);
        Map<String, Object> parsedClaims = JWTUtil.parseToken(token);

        assertNotNull(parsedClaims, "Parsed claims should not be null");
        assertEquals("12345", parsedClaims.get("userId"), "User ID should match");
        assertEquals("testUser", parsedClaims.get("username"), "Username should match");
    }
}
