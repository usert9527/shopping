package com.user9527.shopping.member.feign;

import com.user9527.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/1/16 10:20
 */
@FeignClient("shopping-coupon")
public interface CouponServiceFeign {
    @RequestMapping("coupon/coupon/member/list")
    R memberCoupons();
}
