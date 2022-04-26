package com.chffy.seckill.rabbitmq;

import com.chffy.seckill.pojo.SeckillMessage;
import com.chffy.seckill.pojo.SeckillOrder;
import com.chffy.seckill.pojo.User;
import com.chffy.seckill.service.IGoodsService;
import com.chffy.seckill.service.IOrderService;
import com.chffy.seckill.utils.JsonUtil;
import com.chffy.seckill.vo.GoodsVo;
import com.chffy.seckill.vo.RespBean;
import com.chffy.seckill.vo.RespBeanEnum;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {
    @Autowired
    IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    IOrderService orderService;

    @RabbitListener(queues = "seckillQueue")
    public void receive(String msg) {
        log.info("接收消息：" + msg);

        SeckillMessage message = JsonUtil.jsonStr2Object(msg, SeckillMessage.class);
        Long goodsId = message.getGoodsId();
        User user = message.getUser();

        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goods.getStockCount() < 1) {
            return;
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) valueOperations.get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null)
            return;

        orderService.seckill(user, goods);
    }
}
