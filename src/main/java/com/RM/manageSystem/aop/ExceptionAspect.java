package com.RM.manageSystem.aop;

import com.RM.manageSystem.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;


import static com.RM.manageSystem.controller.ResponseCode.DATABASE_ERR;


/**
 * 攔截 mapper層拋出的異常並封裝成 SystemException
 */
@Slf4j
@Aspect
@Component
public class ExceptionAspect {

    @AfterThrowing(pointcut = "execution(* com.RM.manageSystem.mapper.*.*(..))", throwing = "exception")
    public void handleException(Exception exception) {
        if(exception instanceof DuplicateKeyException){
            log.error("資料庫操作數據驗證失敗: {}", exception.getMessage(), exception);
            throw new SystemException(DATABASE_ERR,"電子郵件與其他人重複囉");
        }
        log.error("資料庫操作異常: {}", exception.getMessage(), exception);
        throw new SystemException(DATABASE_ERR,"system error , please try later");
    }
}

