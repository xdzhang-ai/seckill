package com.chffy.seckill.controller;


import com.chffy.seckill.service.IUserService;
import com.chffy.seckill.vo.LoginVo;
import com.chffy.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author chffy
 * @since 2022-03-17
 */
@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {
    @Autowired
    IUserService userService;

    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        return userService.doLogin(loginVo, request, response);
    }
}
