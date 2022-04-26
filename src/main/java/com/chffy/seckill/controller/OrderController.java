package com.chffy.seckill.controller;


import com.chffy.seckill.pojo.User;
import com.chffy.seckill.service.IOrderService;
import com.chffy.seckill.vo.OrderDetailVo;
import com.chffy.seckill.vo.RespBean;
import com.chffy.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author chffy
 * @since 2022-03-18
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    IOrderService orderService;

    @ResponseBody
    @RequestMapping("/detail")
    public RespBean detail(User user, Long orderId) {
        if (user == null)
            return RespBean.error(RespBeanEnum.SESSION_ERROR);

        OrderDetailVo orderDetailVo = orderService.detail(orderId);
        return RespBean.success(orderDetailVo);
    }
}
