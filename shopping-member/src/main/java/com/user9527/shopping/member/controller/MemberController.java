package com.user9527.shopping.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.user9527.common.exception.BizCodeEnum;
import com.user9527.shopping.member.exception.PhoneExistException;
import com.user9527.shopping.member.exception.UsernameExistException;
import com.user9527.shopping.member.feign.CouponServiceFeign;
import com.user9527.shopping.member.vo.MemberLoginVo;
import com.user9527.shopping.member.vo.MemberRegistVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.user9527.shopping.member.entity.MemberEntity;
import com.user9527.shopping.member.service.MemberService;
import com.user9527.common.utils.PageUtils;
import com.user9527.common.utils.R;


/**
 * 会员
 *
 * @author yangtao
 * @email 2812343437@qq.com
 * @date 2020-12-31 21:09:07
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponServiceFeign couponServiceFeign;

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo) {

        MemberEntity entity = memberService.login(vo);
        if (entity != null) {
            //登录成功
            return R.ok().setData(entity);
        }
        //登录失败
        return R.error(BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(), BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMsg());
    }

    @PostMapping("/regist")
    public R regist(@RequestBody MemberRegistVO vo) {

        //尝试注册
        try {
            memberService.regist(vo);
        } catch (PhoneExistException e) {
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        } catch (UsernameExistException e) {
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(), BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        }
        //成功
        return R.ok();
    }

    @RequestMapping("/coupons")
    public R test() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("李四");

        R coupons = couponServiceFeign.memberCoupons();

        return R.ok().put("member", memberEntity).put("coupons", coupons.get("coupons"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
