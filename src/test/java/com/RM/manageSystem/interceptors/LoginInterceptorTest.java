package com.RM.manageSystem.interceptors;

import com.RM.manageSystem.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(SpringExtension.class)
public class LoginInterceptorTest {

    @InjectMocks
    private LoginInterceptor loginInterceptor;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private Map<String, Object> claims;
    private String token;

    @BeforeEach
    public void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        claims = new HashMap<>();
        claims.put("userId", "1");
        claims.put("username", "testUser");
        token = JWTUtil.genToken(claims);
    }

    @Test
    public void testPreHandle_withValidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", token);

        // Mock Redis token
        when(valueOperations.get(token)).thenReturn(token);

        boolean result = loginInterceptor.preHandle(request, response, new Object());

        assertTrue(result);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testPreHandle_withInvalidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "");

        // Mock Redis token not found
        when(valueOperations.get(token)).thenReturn(null);

        boolean result = loginInterceptor.preHandle(request, response, new Object());

        assertFalse(result);
        assertEquals(401, response.getStatus());
    }
}
