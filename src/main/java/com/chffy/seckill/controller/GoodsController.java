package com.chffy.seckill.controller;

import com.chffy.seckill.pojo.User;
import com.chffy.seckill.service.IGoodsService;
import com.chffy.seckill.service.IUserService;
import com.chffy.seckill.vo.DetailVo;
import com.chffy.seckill.vo.GoodsVo;
import com.chffy.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    IUserService userService;
    @Autowired
    IGoodsService goodsService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @ResponseBody
    @RequestMapping(value = "/toList", produces = "text/html;charset=UTF-8")
    public String toList(Model model, HttpServletRequest request, HttpServletResponse response, User user) {
//        if (StringUtils.isEmpty(ticket))
//            return "login";
////        User user = (User) session.getAttribute(ticket);
//        User user = userService.getUserByCookie(request, response, ticket);
//        if (user == null)
//            return "login";
//
//        log.info("{}", user);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 页面缓存
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context);
        if (!StringUtils.isEmpty(html))
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);

        return html;
    }

    @ResponseBody
    @RequestMapping(value = "/detail/{goodsId}")
    public RespBean toDetail(Model model, HttpServletRequest request, HttpServletResponse response, User user, @PathVariable Long goodsId) {
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int secKillStatus = 0;
        int remainSeconds = 0;
        if (nowDate.before(startDate)) {
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
        } else if (nowDate.after(endDate)) {
            secKillStatus = 2;
            remainSeconds = -1;
        } else {
            secKillStatus = 1;
            remainSeconds = 0;
        }

        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);

        return RespBean.success(detailVo);
    }
}
