package com.example.aihou.exception;

import com.example.aihou.common.ResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String code;
    private final String message;
    private final Object data;

    // 1. 原来的构造方法（保留）
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.PARAM_ERROR.getCode();
        this.message = message;
        this.data = null;
    }

    // 👇 2. 新增这个构造方法！专门用来接收枚举
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
        this.message = resultCode.getMsg();
        this.data = null;
    }
}