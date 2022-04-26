package com.chffy.seckill.vo;

import com.chffy.seckill.pojo.Goods;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class GoodsVo extends Goods {

    private BigDecimal seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
