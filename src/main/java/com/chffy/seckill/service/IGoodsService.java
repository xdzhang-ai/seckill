package com.chffy.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chffy.seckill.pojo.Goods;
import com.chffy.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author chffy
 * @since 2022-03-18
 */
public interface IGoodsService extends IService<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
