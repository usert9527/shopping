package com.user9527.shopping.auth.feign;

import com.user9527.common.utils.R;
import com.user9527.shopping.auth.vo.MemberRegistVo;
import com.user9527.shopping.auth.vo.UserLoginVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/7/18 11:05
 */
@FeignClient("shopping-member")//这个远程服务
@Component
public interface MemberFeignService {

    @PostMapping("/member/member/regist")
    R regist(@RequestBody MemberRegistVo vo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);
}


