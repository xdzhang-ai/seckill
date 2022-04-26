package com.chffy.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chffy.seckill.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author chffy
 * @since 2022-03-17
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
