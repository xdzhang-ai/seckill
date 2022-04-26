package com.chffy.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chffy.seckill.pojo.User;
import com.chffy.seckill.vo.LoginVo;
import com.chffy.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author chffy
 * @since 2022-03-17
 */
public interface IUserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    User getUserByCookie(HttpServletRequest request, HttpServletResponse response, String ticket);

    RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);
}
