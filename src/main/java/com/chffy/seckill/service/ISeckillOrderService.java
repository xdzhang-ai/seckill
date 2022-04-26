package com.chffy.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chffy.seckill.pojo.SeckillOrder;
import com.chffy.seckill.pojo.User;

/**
 * <p>
 * 秒杀订单表 服务类
 * </p>
 *
 * @author chffy
 * @since 2022-03-18
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    Long getResult(User user, Long goodsId);
}
