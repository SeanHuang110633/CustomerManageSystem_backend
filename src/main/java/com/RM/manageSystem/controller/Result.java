package com.RM.manageSystem.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
// 通用數據封裝類
public class Result<T> {
    private Integer code;
    private String message; // 操作訊息
    private T data; // 運載數據

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


    // 用於返回操作成功的結果(帶數據體)
    public static <E> Result<E> success(int code,String message,E data) {
        return new Result<E>(code, message, data);
    }

    // 用於返回操作成功的結果(無數據體)
    public static <E> Result<E> success(int code,String message) {
        return new Result<E>(code, message,null);
    }



    // 用於返回操作失敗的結果
    public static Result error(String message) {
        return new Result(1, message, null);
    }

    public static Result error(int code ,String message) {
        return new Result(code, message, null);
    }

    public static Result<Map<String,String>> error(int code ,String message,Map<String,String> data){
        return new Result<>(code, message, data);
    }


}

