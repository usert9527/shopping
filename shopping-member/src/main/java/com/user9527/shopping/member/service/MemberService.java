package com.user9527.shopping.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user9527.common.utils.PageUtils;
import com.user9527.shopping.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author yangtao
 * @email 2812343437@qq.com
 * @date 2020-12-31 21:09:07
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

