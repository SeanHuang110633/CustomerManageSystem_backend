package com.RM.manageSystem.controller;


import com.RM.manageSystem.pojo.User;
import com.RM.manageSystem.service.UserService;
import com.RM.manageSystem.utils.JWTUtil;
import com.RM.manageSystem.utils.MD5Util;
import com.RM.manageSystem.utils.ThreadLocalUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@Slf4j
@WebMvcTest(UserController.class)
@ExtendWith(SpringExtension.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private RedisOperations<String, String> redisOperations;


    private User mockUser;
    private String token;

    @BeforeEach
    void setUp() {
        //模擬Redis
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
        when(valueOperations.getOperations()).thenReturn(redisOperations);
        when(redisOperations.delete(anyString())).thenReturn(true);

        // 設置測試用的user
        mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("existingUser");
        mockUser.setUserPassword(MD5Util.getMD5("password123"));

        // 設置測試用的JWT token
        HashMap<String, Object> mockClaims = new HashMap<>();
        mockClaims.put("id", mockUser.getId());
        mockClaims.put("username", mockUser.getUsername());
        token = JWTUtil.genToken(mockClaims);

        // 模擬 Redis 操作返回 token
        when(valueOperations.get(anyString())).thenReturn(token);
    }


    @Test
    public void testRegisterUser() throws Exception {
        doNothing().when(userService)
                .register("newUser", "password123");

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("username", "newUser")
                        .param("userPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(20010))
                .andExpect(jsonPath("$.message").value("註冊成功"));

        verify(userService, times(1))
                .register("newUser", "password123");
    }

    @Test
    void testRegisterValidation() throws Exception {
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("username", "n") // Invalid username
                        .param("userPassword", "password123"))
                .andExpect(jsonPath("$.code").value(40031))
                .andExpect(jsonPath("$.message").value("輸入資訊驗未通過驗證，請重試"))
                .andDo(print());

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("username", "newUser") // Invalid username
                        .param("userPassword", "123"))
                .andExpect(jsonPath("$.code").value(40031))
                .andExpect(jsonPath("$.message").value("輸入資訊驗未通過驗證，請重試"))
                .andDo(print());
    }


    @Test
    public void testLogin() throws Exception {

        when(userService.login(mockUser.getUsername(), "password123")).thenReturn(token);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "existingUser")
                        .param("userPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(20010))
                .andExpect(jsonPath("$.message").value("登入成功"))
                .andExpect(jsonPath("$.data").value(token));
    }

    @Test
    public void testGetUserInfo() throws Exception {
        when(userService.findUserByUsername(mockUser.getUsername())).thenReturn(mockUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/userinfo")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(20010))
                .andExpect(jsonPath("$.message").value("查詢用戶資訊成功"));
    }


    @Test
    public void testUpdateUser() throws Exception {
        mockUser.setUsername("updatedUser");
        mockUser.setEmail("test@gmail.com");

        doNothing().when(userService).updateUser(mockUser);

        // 將用戶對象轉換為 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(mockUser);

        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(20010))
                .andExpect(jsonPath("$.message").value("用戶資訊更新成功"));
    }

    @Test
    public void testUpdateUserValidation() throws Exception {
        mockUser.setUsername("updatedUser");
        mockUser.setEmail("wrongEmailForm");

        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(mockUser);

        mockMvc.perform(MockMvcRequestBuilders.put("/user")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40031))
                .andExpect(jsonPath("$.message").value("輸入資訊驗未通過驗證，請重試"));
    }



    @Test
    public void testUpdatePassword() throws Exception {
        Map<String, String> params = new HashMap<>();
        doNothing().when(userService).updatePassword(params, token);

        // 將參數轉換為 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonParams = objectMapper.writeValueAsString(params);

        // 執行 PATCH 請求
        mockMvc.perform(MockMvcRequestBuilders.patch("/user/updatePwd")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParams))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(20010))
                .andExpect(jsonPath("$.message").value("更新用戶密碼成功"))
                .andDo(print());
    }


    @Test
    public void testListUsers() throws Exception {
        List<User> mockUserList = new ArrayList<>();
        User mockUser2 = new User();
        mockUser2.setId(2);
        mockUser2.setUsername("existingUser2");
        mockUserList.add(mockUser);
        mockUserList.add(mockUser2);
        Mockito.when(userService.listUsers()).thenReturn(mockUserList);

        mockMvc.perform(MockMvcRequestBuilders.get("/user").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(20010))
                .andExpect(jsonPath("$.data[0].username").value("existingUser"))
                .andExpect(jsonPath("$.data[1].username").value("existingUser2"));

        verify(userService, times(1)).listUsers();
    }


    @Test
    public void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(mockUser.getId());

        mockMvc.perform(delete("/user")
                        .header("Authorization", token)
                        .param("id", String.valueOf(mockUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("用戶刪除成功"));

        verify(userService, times(1)).deleteUser(mockUser.getId());
    }
}
