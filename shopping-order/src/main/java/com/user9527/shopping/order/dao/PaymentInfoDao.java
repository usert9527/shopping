package com.user9527.shopping.order.dao;

import com.user9527.shopping.order.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author yangtao
 * @email 2812343437@qq.com
 * @date 2020-12-31 21:14:18
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
