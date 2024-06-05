package com.RM.manageSystem.controller;

import com.RM.manageSystem.anno.Log;
import com.RM.manageSystem.pojo.User;
import com.RM.manageSystem.service.UserService;
import com.RM.manageSystem.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.RM.manageSystem.controller.ResponseCode.*;

/**
 * user controller
 */

@Slf4j
@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;


    /**
     * 用戶註冊功能
     *
     * @param username:長度須大於等於2小於20，可使用英文字母（不分大小寫）、數字及特殊符號。
     * @param userPassword:長度必須大於等於6小於24，需使用英文字母（不分大小寫）及數字，允許特殊符號。
     * @return Result 包含註冊成功或錯誤信息。
     */
    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+]{2,20}$") String username,
                           @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()_+]{6,}$") String userPassword
    ) {
        log.info("開始註冊用戶: {}", username);
        userService.register(username, userPassword);
        log.info("用戶註冊成功: {}", username);
        return Result.success(SUCCESS, "註冊成功");
    }

    /**
     * 用戶登入功能
     *
     * @param username
     * @param userPassword
     * @return Result 包含登入成功或錯誤信息。
     */
    @PostMapping("/login")
    public Result<String> login(@Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+]{2,20}$") String username,
                                @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()_+]{6,}$") String userPassword) {
        log.info("開始用戶登入: {}", username);
        String token = userService.login(username, userPassword);
        return Result.success(SUCCESS, "登入成功", token);
    }

    /**
     * 依使用者名稱查詢使用者資訊
     *
     * @return 用戶資訊
     */
    @GetMapping("/userinfo")
    public Result<User> getUserInfo() {
        log.info("查詢用戶資訊");
        Map<String, Object> userClaims = ThreadLocalUtil.get();
        String username = (String) userClaims.get("username");
        User user = userService.findUserByUsername(username);
        return Result.success(SUCCESS, "查詢用戶資訊成功", user);
    }

    /**
     * 更新用戶資訊
     *
     * @param user 用戶對象
     * @return 結果對象
     */
    @Log
    @PutMapping
    public Result updateUser(@RequestBody @Validated User user) {
        log.info("更新用戶資訊: {}", user.getUsername());
        userService.updateUser(user);
        log.info("用戶資訊更新成功: {}", user.getUsername());
        return Result.success(SUCCESS, "用戶資訊更新成功");
    }

    /**
     * 更新用戶密碼
     *
     * @param params 包含舊密碼和新密碼的參數
     * @param token  授權token
     * @return 結果對象
     */
    @PatchMapping("/updatePwd")
    public Result updatePassword(@RequestBody Map<String, String> params,
                                 @RequestHeader("Authorization") String token) {
        log.info("接收更新用戶密碼請求");
        userService.updatePassword(params, token);
        log.info("更新用戶密碼成功");
        return Result.success(SUCCESS, "更新用戶密碼成功");
    }

    /**
     * 獲取用戶列表
     *
     * @return 用戶列表
     */
    @GetMapping
    public Result<List<User>> listUsers() {
        log.info("查詢所有用戶列表");
        List<User> users = userService.listUsers();
        log.info("查詢所有用戶列表成功");
        return Result.success(SUCCESS, "查詢所有系統用戶成功", users);
    }

    /**
     * 更新用戶角色
     *
     * @param id       用戶ID
     * @param userRole 新權限
     * @return 結果對象
     */
    @Log
    @PatchMapping("/updateRole")
    public Result updateRole(@RequestParam Integer id, @RequestParam Integer userRole) {
        log.info("更新用戶權限: 用戶ID={}, 新角色={}", id, userRole);
        userService.updateUserRole(id, userRole);
        log.info("用戶權限更新成功: 用戶ID={}, 新角色={}", id, userRole);
        return Result.success(SUCCESS, "更新用戶角色權限成功");
    }

    /**
     * 刪除用戶(邏輯刪除)
     *
     * @param id 用戶ID
     * @return 結果對象
     */
    @Log
    @DeleteMapping
    public Result deleteUser(@RequestParam Integer id) {
        log.info("刪除用戶: 用戶ID={}", id);
        userService.deleteUser(id);
        log.info("用戶刪除成功: 用戶ID={}", id);
        return Result.success(SUCCESS, "用戶刪除成功");
    }
}
