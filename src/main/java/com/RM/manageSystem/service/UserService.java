package com.RM.manageSystem.service;

import com.RM.manageSystem.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    /**
     * 根據用戶名查詢用戶資訊。
     *
     * @param username 用戶名
     * @return user 用戶對象
     */
    User findUserByUsername(String username);

    /**
     * 註冊用戶
     *
     * @param username 用戶名稱
     * @param userPassword 用戶密碼
     */
    void register(String username, String userPassword);

    /**
     * 用戶登入
     *
     * @param username 用戶名稱
     * @param userPassword 用戶密碼
     * @return JWTToken
     */
    String login(String username, String userPassword);

    /**
     * 更新用戶資訊。
     *
     * @param user 用戶對象
     */
    //更新使用者資訊(username,email)
    void updateUser(User user);

    /**
     * 更新用戶密碼
     *
     * @param params 用戶新舊密碼資訊
     * @param token 用戶JWT token
     */
    //更改使用者密碼
    void updatePassword(Map<String, String> params, String token);


    /**
     * 查詢所有用戶列表。
     *
     * @return 用戶列表
     */
    List<User> listUsers();

    /**
     * 刪除用戶
     *
     * @param id 用戶ID
     */
    void deleteUser(Integer id);

    /**
     * 更新用戶權限。
     *
     * @param id   用戶ID
     * @param userRole 新權限
     */
    void updateUserRole(Integer id, Integer userRole);



}
