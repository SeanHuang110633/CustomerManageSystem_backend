package com.RM.manageSystem.service.imp;


import ch.qos.logback.classic.spi.EventArgUtil;
import com.RM.manageSystem.controller.ResponseCode;
import com.RM.manageSystem.exception.BusinessException;
import com.RM.manageSystem.exception.DatabaseOperationException;
import com.RM.manageSystem.exception.SystemException;
import com.RM.manageSystem.mapper.UserMapper;
import com.RM.manageSystem.pojo.User;
import com.RM.manageSystem.service.UserService;
import com.RM.manageSystem.utils.JWTUtil;
import com.RM.manageSystem.utils.MD5Util;
import com.RM.manageSystem.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.RM.manageSystem.controller.ResponseCode.*;


/**
 * user service
 */
@Slf4j
@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserMapper userMapper;

    //使用Redis儲存JWT token校驗其有效性(排除使用者更換密碼但token還在效期內的問題)
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 根據用戶名查詢用戶資訊。
     *
     * @param username 用戶名
     * @return user 用戶對象
     */
    @Override
    public User findUserByUsername(String username) {
        log.info("依使用者名稱查詢用戶資訊，使用者名稱: {}", username);
        User user = userMapper.findUserByUsername(username);
        if (user == null) {
            log.warn("找不到使用者名稱為 {} 的用戶", username);
            throw new BusinessException(USER_NOT_FOUND, "用戶不存在");
        }
        log.info("查詢用戶資訊成功，使用者名稱: {}", username);
        return user;
    }

    /**
     * 註冊用戶
     *
     * @param username 用戶名稱
     * @param userPassword 用戶密碼
     */
    @Override
    public void register(String username, String userPassword) {
        log.info("註冊新用戶，用戶名稱: {}", username);

        // 檢查用戶名是否已存在
        if (userMapper.findUserByUsername(username) != null) {
            log.warn("註冊失敗，已存在相同使用者名稱: {}", username);
            throw new BusinessException(INVALID_INPUT, "已存在相同使用者名稱");
        }

        String encryptedPassword = MD5Util.getMD5(userPassword);
        int registeredUserNum = userMapper.register(username, encryptedPassword);
        if (registeredUserNum > 0) {
            log.info("註冊新用戶成功，用戶名稱: {}", username);
            return;
        }
        throw new SystemException(SYSTEM_ERR, "系統異常請聯繫管理員");
    }


    /**
     * 用戶登入
     *
     * @param username 用戶名稱
     * @param userPassword 用戶密碼
     * @return JWTToken
     */
    @Override
    public String login(String username, String userPassword) {
        User loginUser = findUserByUsername(username);
        if (loginUser == null) {
            log.warn("登入失敗，用戶名稱錯誤或該用戶不存在: {}", username);
            throw new BusinessException(INVALID_INPUT, "登入失敗，用戶名稱錯誤或該用戶不存在");
        }

        if (MD5Util.getMD5(userPassword).equals(loginUser.getUserPassword())) {
            HashMap<String, Object> userClaims = new HashMap<>();
            userClaims.put("id", loginUser.getId());
            userClaims.put("username", loginUser.getUsername());
            String userToken = JWTUtil.genToken(userClaims);
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(userToken, userToken, 12, TimeUnit.HOURS);
            log.info("登入成功: {}", username);
            return userToken;
        }

        log.warn("登入失敗，密碼錯誤: {}", username);
        throw new BusinessException(INVALID_INPUT, "登入失敗，密碼錯誤");
    }

    /**
     * 更新用戶資訊。
     *
     * @param user 用戶對象
     */
    @Override
    public void updateUser(User user) {
        log.info("更新用戶資訊，用戶ID: {}", user.getUsername());

        user.setUpdateTime(LocalDateTime.now());
        int updatedUserNum = userMapper.updateUser(user);
        if (updatedUserNum > 0) {
            log.info("更新用戶資訊成功，用戶ID: {}", user.getUsername());
            return;
        }
        throw new SystemException(SYSTEM_ERR, "系統異常請聯繫管理員");
    }

    /**
     * 更新用戶密碼
     *
     * @param params 用戶新舊密碼資訊
     * @param token 用戶JWT token
     */
    @Override
    public void updatePassword(Map<String, String> params, String token) {
        log.info("service層更新用戶密碼");
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");

        if (!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)) {
            log.warn("密碼更新失敗，所有欄位都必須輸入");
            throw new BusinessException(INVALID_INPUT, "所有欄位都必須輸入");
        }

        //取得當前用戶資訊
        Map<String, Object> userClaims = ThreadLocalUtil.get();
        String username = (String) userClaims.get("username");
        User loginUser = findUserByUsername(username);

        if (!loginUser.getUserPassword().equals(MD5Util.getMD5(oldPwd))) {
            log.warn("密碼更新失敗，舊密碼輸入錯誤: {}", username);
            throw new BusinessException(INVALID_INPUT, "舊密碼輸入錯誤");
        }

        if (!newPwd.equals(rePwd)) {
            log.warn("密碼更新失敗，新密碼與確認密碼不相符: {}", username);
            throw new BusinessException(INVALID_INPUT, "新密碼與確認密碼不相符");
        }

        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.getOperations().delete(token);
            log.info("清除使用者原有的token成功");
        } catch (Exception e) {
            throw new SystemException(SYSTEM_ERR, "系統異常請聯繫管理員", e);
        }

        Integer id = loginUser.getId();
        int updatedPassword = userMapper.updatePassword(MD5Util.getMD5(newPwd), id);
        if (updatedPassword > 0) {
            log.info("密碼更新成功: {}", username);
            return;
        }

        log.error("更新用戶密碼失敗，使用者ID: {}", id);
        throw new SystemException(SYSTEM_ERR, "系統異常請聯繫管理員");
    }


    /**
     * 查詢所有用戶列表。
     *
     * @return 用戶列表
     */
    @Override
    public List<User> listUsers() {
        log.info("查詢所有用戶列表");
        List<User> users = userMapper.listUsers();

        if (users != null) {
            log.info("查詢所有用戶列表成功");
            return users;
        }

        log.info("查詢所有用戶列表失敗");
        throw new SystemException(SYSTEM_ERR, "系統異常請聯繫管理員");
    }

    /**
     * 刪除用戶。
     *
     * @param id 用戶ID
     */
    @Override
    public void deleteUser(Integer id) {
        log.info("刪除用戶，使用者ID: {}", id);
        int deletedUserNum = userMapper.deleteUser(id);
        if (deletedUserNum > 0) {
            log.info("刪除用戶成功，使用者ID: {}", id);
            return;
        }
        log.error("刪除用戶失敗，使用者ID: {}", id);
        throw new SystemException(SYSTEM_ERR, "系統異常請聯繫管理員");
    }

    /**
     * 更新用戶權限。
     *
     * @param id   用戶ID
     * @param userRole 新權限
     */
    @Override
    public void updateUserRole(Integer id, Integer userRole) {
        log.info("更新用戶權限，使用者ID: {}, 新權限: {}", id, userRole);
        int updatedRole = userMapper.updateUserRole(id, userRole);
        if (updatedRole > 0) {
            log.info("更新用戶權限成功，使用者ID: {}, 新權限: {}", id, userRole);
            return;
        }
        log.error("更新用戶權限失敗，使用者ID: {}, 新權限: {}", id, userRole );
        throw new SystemException(SYSTEM_ERR, "系統異常請聯繫管理員");
    }
}
