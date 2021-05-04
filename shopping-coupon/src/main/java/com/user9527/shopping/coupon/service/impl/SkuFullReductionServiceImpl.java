package com.user9527.shopping.coupon.service.impl;

import com.user9527.common.to.MemberPrice;
import com.user9527.common.to.SkuReductionTO;
import com.user9527.shopping.coupon.entity.MemberPriceEntity;
import com.user9527.shopping.coupon.entity.SkuLadderEntity;
import com.user9527.shopping.coupon.service.MemberPriceService;
import com.user9527.shopping.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user9527.common.utils.PageUtils;
import com.user9527.common.utils.Query;

import com.user9527.shopping.coupon.dao.SkuFullReductionDao;
import com.user9527.shopping.coupon.entity.SkuFullReductionEntity;
import com.user9527.shopping.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTO skuReductionTO) {
        //  sku的优惠，满减等信息 gulimall_sms -> sms_sku_ladder -> sms_sku_full_reduction -> sms_member_price
        // sms_sku_ladder 商品阶梯价格
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTO.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTO.getFullCount());
        skuLadderEntity.setDiscount(skuReductionTO.getDiscount());
        skuLadderEntity.setAddOther(skuReductionTO.getCountStatus());

        if(skuReductionTO.getFullCount() > 0){
            skuLadderService.save(skuLadderEntity);
        }

        // sms_sku_full_reduction 商品满减信息
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTO, skuFullReductionEntity);

        if(skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal("0")) == 1){
            this.save(skuFullReductionEntity);
        }

        // sms_member_price
        List<MemberPrice> memberPrice = skuReductionTO.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrice.stream().map(item -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTO.getSkuId());
            memberPriceEntity.setMemberLevelId(item.getId());
            memberPriceEntity.setMemberLevelName(item.getName());
            memberPriceEntity.setMemberPrice(item.getPrice());
            memberPriceEntity.setAddOther(1);

            return memberPriceEntity;
        }).filter(item -> {
            return item.getMemberPrice().compareTo(new BigDecimal("0")) == 1;
        }).collect(Collectors.toList());

        memberPriceService.saveBatch(collect);

    }

}