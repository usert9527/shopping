package com.user9527.shopping.product.dao;

import com.user9527.common.constant.ProductConstant;
import com.user9527.shopping.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author yangtao
 * @email 2812343437@qq.com
 * @date 2020-12-29 21:04:26
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}
