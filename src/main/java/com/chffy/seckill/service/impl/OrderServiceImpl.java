package com.chffy.seckill.service.impl;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chffy.seckill.exception.GlobalException;
import com.chffy.seckill.mapper.OrderMapper;
import com.chffy.seckill.pojo.Order;
import com.chffy.seckill.pojo.SeckillGoods;
import com.chffy.seckill.pojo.SeckillOrder;
import com.chffy.seckill.pojo.User;
import com.chffy.seckill.service.IGoodsService;
import com.chffy.seckill.service.IOrderService;
import com.chffy.seckill.service.ISeckillGoodsService;
import com.chffy.seckill.service.ISeckillOrderService;
import com.chffy.seckill.utils.MD5Util;
import com.chffy.seckill.utils.UUIDUtil;
import com.chffy.seckill.vo.GoodsVo;
import com.chffy.seckill.vo.OrderDetailVo;
import com.chffy.seckill.vo.RespBean;
import com.chffy.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chffy
 * @since 2022-03-18
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    ISeckillGoodsService seckillGoodsService;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    ISeckillOrderService seckillOrderService;
    @Autowired
    IGoodsService goodsService;
    @Autowired
    RedisTemplate redisTemplate;

    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goods) {
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        boolean goodsResult = seckillGoodsService.update(
                new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count-1").eq(
                        "goods_id", seckillGoods.getGoodsId()
                ).gt("stock_count", 0)
        );
        if (seckillGoods.getStockCount() < 1) {
            redisTemplate.opsForValue().set("isStockEmpty:"+goods.getId(), 0);
            return null;
        }

        Order order = new Order();
//        order.setId(0L);
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goods.getGoodsPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
//        order.setPayDate(new Date());
        orderMapper.insert(order);

        SeckillOrder seckillOrder = new SeckillOrder();
//        seckillOrder.setId();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        redisTemplate.opsForValue().set("order:"+user.getId()+":"+goods.getId(), seckillOrder);

        return order;

    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        if (orderId == null)
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);

        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());

        OrderDetailVo detailVo = new OrderDetailVo();
        detailVo.setGoodsVo(goodsVo);
        detailVo.setOrder(order);

        return detailVo;

    }

    @Override
    public String createPath(User user, Long goodsId) {
        String path = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:"+user.getId()+":"+goodsId, path, 60, TimeUnit.SECONDS);
        return path;
    }

    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if (StringUtils.isEmpty(path))
            return false;
        String str = (String) redisTemplate.opsForValue().get("seckillPath:"+user.getId()+":"+goodsId);
        return path.equals(str);
    }

    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (user == null || StringUtils.isEmpty(captcha) || goodsId < 0)
            return false;
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:"+user.getId()+":"+goodsId);
        return captcha.equals(redisCaptcha);
    }
}
