package com.jypt.common;

import lombok.Data;

/**
 * 反馈结果类
 * @param <T>
 */
@Data
public class Result<T> {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 响应消息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应（携带数据）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 包含数据的成功响应结果
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（仅携带消息）
     *
     * @param message 响应消息
     * @param <T>     数据类型
     * @return 包含消息的成功响应结果
     */
    public static <T> Result<T> success(String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        return result;
    }

    /**
     * 错误响应（默认状态码500）
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 包含错误消息的响应结果
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    /**
     * 错误响应（自定义状态码）
     *
     * @param code    自定义状态码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 包含状态码和错误消息的响应结果
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
