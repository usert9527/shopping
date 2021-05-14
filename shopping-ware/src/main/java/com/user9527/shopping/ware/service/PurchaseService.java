package com.user9527.shopping.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user9527.common.utils.PageUtils;
import com.user9527.shopping.ware.entity.PurchaseEntity;
import com.user9527.shopping.ware.vo.MergeVO;
import com.user9527.shopping.ware.vo.PurchaseDoneVO;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author yangtao
 * @email 2812343437@qq.com
 * @date 2020-12-31 21:22:03
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceiveList(Map<String, Object> params);

    void mergepurchase(MergeVO mergeVO);

    void received(List<Long> ids);

    void done(PurchaseDoneVO doneVo);
}

