package com.chffy.seckill.exception;

import com.chffy.seckill.vo.RespBean;
import com.chffy.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException{

    @ExceptionHandler(Exception.class)
    public RespBean exceptionHandler(Exception e){
        if (e instanceof GlobalException) {
            GlobalException ex = (GlobalException) e;
            return RespBean.error(ex.getRespBeanEnum());
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            RespBean respBean = RespBean.error(RespBeanEnum.BIND_ERROR);
            respBean.setMessage(respBean.getMessage()+":"+ex.getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }
        return RespBean.error(RespBeanEnum.ERROR);
    }
}
