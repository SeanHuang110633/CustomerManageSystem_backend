package com.RM.manageSystem.interceptors;

import com.RM.manageSystem.exception.BusinessException;
import com.RM.manageSystem.exception.SystemException;
import com.RM.manageSystem.utils.JWTUtil;
import com.RM.manageSystem.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

import static com.RM.manageSystem.controller.ResponseCode.TOKEN_EXPIRED;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userToken = request.getHeader("Authorization");
        // 驗證token
        try {
            log.info("userToken state {}",userToken);
            //從redis中取出相同token,
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String token = operations.get(userToken);
            if (token == null) {
                log.error("redis token is expired ");
                //token已失效
                throw new BusinessException(TOKEN_EXPIRED,"請重新登入");
            }
            // 解析 JWT 令牌
            Map<String, Object> userClaims = JWTUtil.parseToken(userToken);
            // 把用戶數據存到threadLocal中
            ThreadLocalUtil.set(userClaims);
            // 驗證成功並放行
            return true;
        } catch (Exception e) {
            log.error("未授權的訪問，返回狀態碼401");
            // 驗證失敗，設置HTTP響應狀態為401未授權
            response.setStatus(401);
            // 拒絕放行
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清空ThreadLocal中的數據
        ThreadLocalUtil.remove();
    }
}
