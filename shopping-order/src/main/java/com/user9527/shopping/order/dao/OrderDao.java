package com.user9527.shopping.order.dao;

import com.user9527.shopping.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author yangtao
 * @email 2812343437@qq.com
 * @date 2020-12-31 21:14:18
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
