package com.chffy.seckill.vo;

import com.chffy.seckill.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVo {
    private GoodsVo goodsVo;
    private Order order;
}
