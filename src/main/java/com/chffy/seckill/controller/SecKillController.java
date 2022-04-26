package com.chffy.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chffy.seckill.config.AccessLimit;
import com.chffy.seckill.exception.GlobalException;
import com.chffy.seckill.pojo.Order;
import com.chffy.seckill.pojo.SeckillMessage;
import com.chffy.seckill.pojo.SeckillOrder;
import com.chffy.seckill.pojo.User;
import com.chffy.seckill.rabbitmq.MQSender;
import com.chffy.seckill.service.IGoodsService;
import com.chffy.seckill.service.IOrderService;
import com.chffy.seckill.service.ISeckillOrderService;
import com.chffy.seckill.utils.JsonUtil;
import com.chffy.seckill.vo.GoodsVo;
import com.chffy.seckill.vo.RespBean;
import com.chffy.seckill.vo.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {
    @Autowired
    IGoodsService goodsService;
    @Autowired
    ISeckillOrderService seckillOrderService;
    @Autowired
    IOrderService orderService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    MQSender mqSender;

    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response) {
        if (null==user||goodsId<0){
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // 设置请求头为输出图片类型
        response.setContentType("image/jpg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0); //生成验证码，将结果放入redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId,captcha.text(),300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败",e.getMessage());
        }
    }

    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @RequestMapping("/path")
    @ResponseBody
    public RespBean getSecKillPath(User user, Long goodsId, String captcha) {
        if (user == null)
            return RespBean.error(RespBeanEnum.SESSION_ERROR);

        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check)
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);

        String path = orderService.createPath(user, goodsId);
        return RespBean.success(path);
    }

    @RequestMapping("/result")
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    @RequestMapping("/doSeckill")
    public String doSecKill2(Model model, User user, Long goodsId) {
        if (user == null)
            return "login";
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goods.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }

        SeckillOrder seckillOrder = seckillOrderService.getOne(
                new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId)
        );

        if (seckillOrder != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            return "secKillFail";
        }

        Order order = orderService.seckill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "orderDetail";
    }

    @ResponseBody
    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    public RespBean doSecKill(@PathVariable String path, User user, Long goodsId) {
        if (user == null)
            return RespBean.error(RespBeanEnum.SESSION_ERROR);

//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//        if (goods.getStockCount() < 1) {
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
//        }
//
////        SeckillOrder seckillOrder = seckillOrderService.getOne(
////                new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId)
////        );
//        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
//
//        if (seckillOrder != null) {
//            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
//        }
//
//        Order order = orderService.seckill(user, goods);
//        return RespBean.success(order);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        boolean check = orderService.checkPath(user,goodsId,path);
        if (!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) valueOperations.get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null)
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);

        if (EmptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        if (stock < 0) {
            EmptyStockMap.put(goodsId, true);
            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        SeckillMessage message = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(message));

        return RespBean.success(0);

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list))
            return;
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });
    }
}
