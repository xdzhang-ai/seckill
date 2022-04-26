package com.chffy.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public enum RespBeanEnum {
    SUCCESS(200,"SUCCESS"),
    ERROR(500, "服务端异常"),

    LOGIN_ERROR(500210, "用户名或密码错误"),
    MOBILE_ERROR(500211, "手机格式错误"),
    BIND_ERROR(500212, "参数校验错误"),
    MOBILE_NOT_EXIST(500213, "手机号码不存在"),
    PASSWORD_UPDATE_ERROR(500214, "密码更新失败"),
    SESSION_ERROR(500215, "用户不存在"),
    ACCESS_LIMIT_REACHED(500216, "访问次数过多，请稍后访问"),

    EMPTY_STOCK(500500, "库存不足"),
    REPEAT_ERROR(500501, "每人限购一件"),
    REQUEST_ILLEGAL(500502, "非法请求"),
    ERROR_CAPTCHA(500503, "验证码错误，请重新输入"),

    ORDER_NOT_EXIST(500300, "订单不存在");

    private final Integer code;
    private final String message;
}
