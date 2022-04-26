package com.chffy.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chffy.seckill.mapper.GoodsMapper;
import com.chffy.seckill.pojo.Goods;
import com.chffy.seckill.service.IGoodsService;
import com.chffy.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author chffy
 * @since 2022-03-18
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    GoodsMapper goodsMapper;

    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }
}
