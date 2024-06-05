package com.RM.manageSystem.exception;


import com.RM.manageSystem.controller.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.RM.manageSystem.controller.ResponseCode.SYSTEM_ERR;
import static com.RM.manageSystem.controller.ResponseCode.VALIDATION_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 資料驗證異常(驗證複雜對象)
     *
     * @param ex
     * @return Result.error
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationException(MethodArgumentNotValidException ex) {
        String methodName = Objects.requireNonNull(ex.getParameter().getMethod()).getName();
        String className = ex.getParameter().getMethod().getDeclaringClass().getName();
        log.error("輸入資訊驗未通過驗證 - 方法: {}.{}", className, methodName, ex);
        return Result.error(VALIDATION_ERROR, "輸入資訊驗未通過驗證，請重試");

    }

    /**
     * 資料驗證異常(驗證單個參數)
     *
     * @param ex
     * @return Result.error
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        });
        log.info("輸入資訊驗未通過驗證: " + errors);
        return Result.error(VALIDATION_ERROR, "輸入資訊驗未通過驗證，請重試");
    }

    /**
     * 業務異常
     *
     * @param e BusinessException
     * @return Result.error
     */
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        log.error("BusinessException: ", e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 系統異常
     *
     * @param e SystemException
     * @return Result.error
     */
    @ExceptionHandler(SystemException.class)
    public Result handleSystemException(SystemException e) {
        log.error("SystemException: ", e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 其他異常
     *
     * @param e Exception
     * @return Result.error
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("其他系統異常", e);
        return Result.error(SYSTEM_ERR,"操作失敗，請聯繫系統管理員");
    }

}
