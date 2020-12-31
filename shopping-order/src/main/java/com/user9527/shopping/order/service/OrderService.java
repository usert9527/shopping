package com.user9527.shopping.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user9527.common.utils.PageUtils;
import com.user9527.shopping.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author yangtao
 * @email 2812343437@qq.com
 * @date 2020-12-31 21:14:18
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

