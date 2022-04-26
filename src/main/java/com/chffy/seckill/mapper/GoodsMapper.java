package com.chffy.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chffy.seckill.pojo.Goods;
import com.chffy.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author chffy
 * @since 2022-03-18
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
