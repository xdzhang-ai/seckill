package com.chffy.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chffy.seckill.exception.GlobalException;
import com.chffy.seckill.mapper.UserMapper;
import com.chffy.seckill.pojo.User;
import com.chffy.seckill.service.IUserService;
import com.chffy.seckill.utils.CookieUtil;
import com.chffy.seckill.utils.MD5Util;
import com.chffy.seckill.utils.UUIDUtil;
import com.chffy.seckill.vo.LoginVo;
import com.chffy.seckill.vo.RespBean;
import com.chffy.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author chffy
 * @since 2022-03-17
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        // //参数校验
        // if (StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)){
        // 	return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        // }
        // if (!ValidatorUtil.isMobile(mobile)){
        // 	return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        // }
        //根据手机号获取用户
        User user = userMapper.selectById(mobile);
        if (null == user) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //判断密码是否正确
        if (!MD5Util.formPassToDBPass(password, user.getSalt()).equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //生成cookie
        String ticket = UUIDUtil.uuid();
        //将用户信息存入redis中
        redisTemplate.opsForValue().set("user:" + ticket, user);
        // request.getSession().setAttribute(ticket,user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(HttpServletRequest request, HttpServletResponse response, String ticket) {
        if (StringUtils.isEmpty(ticket)) {
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        if (user != null) {
            CookieUtil.setCookie(request, response, "userTicket", ticket);
        }
        return user;
    }

    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(request, response, userTicket);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }

        user.setPassword(MD5Util.inputPassToDbPass(password, user.getSalt()));

        int result = userMapper.updateById(user);
        if (result == 1) {
            redisTemplate.delete("user:"+userTicket);
            return RespBean.success();
        }

        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_ERROR);
    }
}
