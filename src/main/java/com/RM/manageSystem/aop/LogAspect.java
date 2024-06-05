package com.RM.manageSystem.aop;


import com.RM.manageSystem.mapper.OperationMapper;
import com.RM.manageSystem.pojo.OperationLog;
import com.RM.manageSystem.utils.ThreadLocalUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Component
@Aspect
public class LogAspect {

    @Autowired
    private OperationMapper operationMapper;

    @Around("@annotation(com.RM.manageSystem.anno.Log)")
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {

        //獲得當前使用者Id
        Map<String, Object> userClaims = ThreadLocalUtil.get();
        Integer operationUser = (Integer) userClaims.get("id");

        //獲得時間
        LocalDateTime operationTime = LocalDateTime.now();

        //獲得操作的類名
        String operationClassName = joinPoint.getTarget().getClass().getName();

        //獲得操作的方法名
        String operationFunctionName = joinPoint.getSignature().getName();

        //獲得操作方法的參數
        Object[] args = joinPoint.getArgs();
        String operationParams = Arrays.toString(args);

        long startTime = System.currentTimeMillis();
        //調用原始目標方法
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        //方法的返回值
        String operationReturnValue = JSONObject.toJSONString(result);
        //操作執行時間
        Long operationDuration = endTime - startTime;

        OperationLog operationLog = new OperationLog(null,operationUser,operationTime,operationClassName,
                operationFunctionName,operationParams,operationReturnValue,operationDuration);
        operationMapper.insert(operationLog);

        //最後再增刪改的方法上controller加入@Log註解
        log.info("AOP紀錄操作系統日誌: {}",operationLog);

        return result;
    }
}
