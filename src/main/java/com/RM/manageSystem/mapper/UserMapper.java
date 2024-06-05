package com.RM.manageSystem.mapper;


import com.RM.manageSystem.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 依據username查詢對應user
     *
     * @param username 用戶名稱
     * @return User 用戶
     */
    @Select("select * from system_user where username = #{username}")
    User findUserByUsername(String username);

    /**
     * 用戶註冊(新增)
     *
     * @param username          用戶名
     * @param encryptedPassword 用戶密碼
     * @return int 新增行數
     */
    @Insert("insert into system_user(username,user_password,create_time,update_time)" +
            " values(#{username},#{encryptedPassword},now(),now())")
    int register(String username, String encryptedPassword);


    /**
     * 更新用戶資訊
     *
     * @param user 用戶對象
     * @return int 更新行數
     */
    @Update("update system_user set username = #{username},email = #{email},update_time = #{updateTime} where id = #{id}")
    int updateUser(User user);

    /**
     * 刪除用戶
     *
     * @param id 用戶id
     * @return int 刪除行數
     */

    @Update("update system_user set isDelete = 1, update_time = now() where id = #{id}")
    int deleteUser(Integer id);

    /**
     * 查詢所有用戶
     *
     * @return List<User> 用戶列表
     */
    @Select("select * from system_user where isDelete = 0")
    List<User> listUsers();

    //更新使用者權限(只有老闆有這個功能)

    /**
     * 更新用戶權限
     *
     * @param id 用戶id
     * @param userRole 用戶權限
     * @return 更新的行數
     */
    @Update("update system_user set user_role = #{userRole},update_time=now() where id = #{id}")
    int updateUserRole(Integer id, Integer userRole);


    /**
     * 更新用戶密碼
     * @param encryptedPassword 用戶密碼
     * @param id 用戶id
     * @return 更新的行數
     */
    @Update("update system_user set user_password = #{encryptedPassword},update_time=now() where id = #{id}")
    int updatePassword(String encryptedPassword, Integer id);
}
