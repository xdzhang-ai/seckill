package com.chffy.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chffy.seckill.pojo.Order;
import com.chffy.seckill.pojo.User;
import com.chffy.seckill.vo.GoodsVo;
import com.chffy.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chffy
 * @since 2022-03-18
 */
public interface IOrderService extends IService<Order> {

    Order seckill(User user, GoodsVo goods);

    OrderDetailVo detail(Long orderId);

    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId, String path);

    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
