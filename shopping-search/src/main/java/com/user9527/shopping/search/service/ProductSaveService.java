package com.user9527.shopping.search.service;

import com.user9527.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/5/31 15:41
 */
public interface ProductSaveService {
    boolean saveProductAsIndices(List<SkuEsModel> skuEsModels) throws IOException;
}
