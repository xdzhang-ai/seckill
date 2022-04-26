package com.chffy.seckill.controller;


import com.chffy.seckill.pojo.User;
import com.chffy.seckill.rabbitmq.MQSender;
import com.chffy.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author chffy
 * @since 2022-03-17
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    MQSender mqSender;

    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user) {
        return RespBean.success(user);
    }

}
