package com.RM.manageSystem.service.imp;

import com.RM.manageSystem.exception.BusinessException;
import com.RM.manageSystem.exception.SystemException;
import com.RM.manageSystem.mapper.UserMapper;
import com.RM.manageSystem.pojo.User;
import com.RM.manageSystem.service.imp.UserServiceImp;
import com.RM.manageSystem.utils.JWTUtil;
import com.RM.manageSystem.utils.MD5Util;
import com.RM.manageSystem.utils.ThreadLocalUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceImpTest {
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImp userService;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> operations;

    @Mock
    private RedisOperations<String, String> redisOperations;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        String encryptedPwd = MD5Util.getMD5("password123");

        mockUser = User.builder().id(1).username("testUser")
                .userPassword(encryptedPwd).userRole(2).build();

        when(userMapper.findUserByUsername("testUser")).thenReturn(mockUser);
    }

    @Test
    void testFindUserByUsernameSuccess() {
        User result = userService.findUserByUsername("testUser");

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        verify(userMapper, times(1)).findUserByUsername("testUser");
    }

    @Test
    void testFindUserByUsernameFailed() {
        when(userMapper.findUserByUsername("nonexistentUser")).thenReturn(null);

        BusinessException thrown = assertThrows(
                BusinessException.class,
                () -> userService.findUserByUsername("nonexistentUser")
        );

        assertEquals("用戶不存在", thrown.getMessage());

        verify(userMapper, times(1)).findUserByUsername("nonexistentUser");
    }


    @Test
    void testRegisterSuccess() {
        when(userMapper.findUserByUsername("newUser")).thenReturn(null);
        when(userMapper.register(anyString(), anyString())).thenReturn(1);

        userService.register("newUser", "password123");

        verify(userMapper, times(1)).findUserByUsername("newUser");
        verify(userMapper, times(1)).register(eq("newUser"), anyString());
    }


    @Test
    void testRegisterUsernameExists() {
        BusinessException thrown = assertThrows(
                BusinessException.class,
                () -> userService.register("testUser", "password123")
        );

        assertEquals("已存在相同使用者名稱", thrown.getMessage());

        verify(userMapper, times(1)).findUserByUsername("testUser");
        verify(userMapper, never()).register(anyString(), anyString());
    }

    @Test
    void testRegisterSystemException() {
        when(userMapper.findUserByUsername("newUser")).thenReturn(null);
        when(userMapper.register(anyString(), anyString())).thenReturn(0);

        SystemException thrown = assertThrows(
                SystemException.class,
                () -> userService.register("newUser", "password123")
        );

        assertEquals("系統異常請聯繫管理員", thrown.getMessage());

        verify(userMapper, times(1)).findUserByUsername("newUser");
        verify(userMapper, times(1)).register(eq("newUser"), anyString());
    }


    @Test
    void testLoginSuccess() {
        //模擬Redis
        when(stringRedisTemplate.opsForValue()).thenReturn(operations);
        doNothing().when(operations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        String result = userService.login("testUser", "password123");

        assertNotNull(result);
        verify(userMapper, times(1)).findUserByUsername("testUser");
    }

    @Test
    void testLoginFailed(){
        when(stringRedisTemplate.opsForValue()).thenReturn(operations);
        doNothing().when(operations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        BusinessException thrown = assertThrows(
                BusinessException.class,
                () -> userService.login("testUser", "wrongPassword123")
        );

        assertEquals("登入失敗，密碼錯誤", thrown.getMessage());
        verify(userMapper, times(1)).findUserByUsername("testUser");
    }

    @Test
    void testUpdateUserSuccess() {
        mockUser.setUsername("updateUser");
        mockUser.setEmail("test@gmail.com");

        when(userMapper.updateUser(mockUser)).thenReturn(1);
        userService.updateUser(mockUser);

        verify(userMapper, times(1)).updateUser(mockUser);
    }

    @Test
    void testUpdateUserFailed() {
        mockUser.setUsername("updateUser");
        mockUser.setEmail("wrongEmailForm");
        when(userMapper.updateUser(mockUser)).thenReturn(0);

        SystemException thrown = assertThrows(
                SystemException.class,
                () -> userService.updateUser(mockUser)
        );

        assertEquals("系統異常請聯繫管理員", thrown.getMessage());
        verify(userMapper, times(1)).updateUser(mockUser);
    }

    @Test
    void testUpdatePassword() {
        //準備參數 param
        Map<String, String> params = new HashMap<>();
        params.put("old_pwd","password123");
        params.put("new_pwd","newPassword123");
        params.put("re_pwd","newPassword123");
        //準備參數 token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", mockUser.getId());
        claims.put("username", mockUser.getUsername());
        String token = JWTUtil.genToken(claims);

        //設定ThreadLocal的值
        ThreadLocalUtil.set(claims);

        when(stringRedisTemplate.opsForValue()).thenReturn(operations);
        when(operations.getOperations()).thenReturn(redisOperations);
        when(redisOperations.delete(token)).thenReturn(true);

        when(userMapper.updatePassword(MD5Util.getMD5("newPassword123"),mockUser.getId())).thenReturn(1);
        userService.updatePassword(params,token );

        verify(userMapper, times(1)).updatePassword(MD5Util.getMD5("newPassword123"), mockUser.getId());
        verify(operations.getOperations(), times(1)).delete(token);
    }

    @Test
    void testListUsers() {
        List<User> mockUsers = Arrays.asList(new User(), new User());

        when(userMapper.listUsers()).thenReturn(mockUsers);

        List<User> result = userService.listUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testDeleteUser() {
        when(userMapper.deleteUser(mockUser.getId())).thenReturn(1);

        userService.deleteUser(mockUser.getId());

        verify(userMapper, times(1)).deleteUser(mockUser.getId());
    }

    @Test
    void testUpdateRole() {
        mockUser.setUserRole(1);
        when(userMapper.updateUserRole(mockUser.getId(),mockUser.getUserRole())).thenReturn(1);

        userService.updateUserRole(mockUser.getId(),mockUser.getUserRole());

        verify(userMapper, times(1)).updateUserRole(mockUser.getId(),mockUser.getUserRole());
    }
}
