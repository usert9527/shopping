package com.user9527.shopping.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.user9527.common.constant.ProductConstant;
import com.user9527.common.to.SkuHasStockTO;
import com.user9527.common.to.SkuReductionTO;
import com.user9527.common.to.SpuBoundTO;
import com.user9527.common.to.es.SkuEsModel;
import com.user9527.common.utils.R;
import com.user9527.common.vo.SkuHasStockVO;
import com.user9527.shopping.product.entity.*;
import com.user9527.shopping.product.feign.CouponFeignService;
import com.user9527.shopping.product.feign.SearchFeignService;
import com.user9527.shopping.product.feign.WareFeignService;
import com.user9527.shopping.product.service.*;
import com.user9527.shopping.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.user9527.common.utils.PageUtils;
import com.user9527.common.utils.Query;

import com.user9527.shopping.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuInfo(SpuSaveVO vo) {

        // 1 保存spu基本信息  pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        // 2 保存spu的描述图片 pms_spu_images
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", vo.getDecript()));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        // 3 保存spu的图片集 pms_spu_info_desc
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        // 4 保存spu的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity byId = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(byId.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(spuInfoEntity.getId());

            return valueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);

        // 5 保存spu的积分信息 sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTO spuBoundTO = new SpuBoundTO();
        BeanUtils.copyProperties(bounds, spuBoundTO);
        spuBoundTO.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTO);
        if(r.getCode() != 0){
            log.error("远程保存spu积分信息失败");
        }


        List<Skus> skus = vo.getSkus();
        if(skus != null && skus.size() > 0){
            skus.forEach(sku -> {
                // 默认图片
                String defaultImg = "";
                for (Images image : sku.getImages()) {
                    if(image.getDefaultImg() == 1){
                        defaultImg = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(vo.getBrandId());
                skuInfoEntity.setCatalogId(vo.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                // 6 保存当前spu对应的所有sku信息
                // 6.1 sku的基本信息 pms_sku_info
                skuInfoService.saveSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> imagesEntities = sku.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());

                    return skuImagesEntity;
                }).filter(entity -> {
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                // 6.2 sku的图片信息 pms_spu_images
                skuImagesService.saveBatch(imagesEntities);

                // 6.3 sku的销售属性信息 pms_sku_sale_attr_value
                List<Attr> attrs = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntitys = attrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);

                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());

                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntitys);

                // 6.4 sku的优惠，满减等信息 gulimall_sms -> sms_sku_ladder -> sms_sku_full_reduction -> sms_member_price
                SkuReductionTO skuReductionTO = new SkuReductionTO();
                BeanUtils.copyProperties(sku, skuReductionTO);
                skuReductionTO.setSkuId(skuId);

                if(skuReductionTO.getFullCount() >0 || skuReductionTO.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTO);
                    if(r1.getCode() != 0){
                        log.error("远程保存sku优惠信息失败");
                    }
                }


            });
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and(w ->{
                w.eq("id", key).or().like("spu_name", key);
            });
        }

        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            queryWrapper.and(w ->{
                w.eq("publish_status", status);
            });
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            queryWrapper.and(w ->{
                w.eq("brand_id", brandId);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){
            queryWrapper.and(w ->{
                w.eq("catalog_id", catelogId);
            });
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        // 1，查出当前spuid对应的所有sku信息，品牌名字
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIdList = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        // TODO 4，查询当前sku的所有可以被用来检索的规格属性
        List<ProductAttrValueEntity> baseAttrlist = productAttrValueService.baseAttrlistforspu(spuId);
        List<Long> attrIds = baseAttrlist.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);
        // 此处使用HashSet.contains  ArrayList.contains 会遍历自身
        Set<Long> setId = new HashSet<>(searchAttrIds);
        List<SkuEsModel.Attrs> attrsList = baseAttrlist.stream().filter(item -> {
            return setId.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attrs);
            return attrs;
        }).collect(Collectors.toList());

        // TODO 1，发送远程调用，库存系统查询是否有库存
        Map<Long, Boolean> stockMap = null;
        try {
            R r = wareFeignService.getSkuHasStock(skuIdList);
            TypeReference<List<SkuHasStockVO>> reference = new TypeReference<List<SkuHasStockVO>>() {
            };
            stockMap = r.getData(reference).stream().collect(Collectors.toMap(SkuHasStockVO::getSkuId, item -> item.getHasStock()));
        }catch (Exception e){
            log.error("库存服务查询异常：原因{}", e);
        }

        // 2，封装每个sku信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> esModels = skus.stream().map(skuInfoEntity -> {
            //组装需要的数据
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(skuInfoEntity, esModel);
            esModel.setSkuPrice(skuInfoEntity.getPrice());
            esModel.setSkuImg(skuInfoEntity.getSkuDefaultImg());
            if(CollectionUtils.isEmpty(finalStockMap)){
                esModel.setHasStock(true);
            }else {
                esModel.setHasStock(finalStockMap.get(skuInfoEntity.getSkuId()));
            }
            // TODO 2，热度评分。0
            esModel.setHotScore(0L);
            // TODO 3，查询品牌和分类的名字
            BrandEntity brand = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());

            CategoryEntity category = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(category.getName());

            esModel.setAttrs(attrsList);
            return esModel;
        }).collect(Collectors.toList());

        // TODO 5，将数据发送给es进行保存
        R r = searchFeignService.saveProductAsIndices(esModels);
        if(r.getCode() == 0){
            // TODO 6, 修改当前spu的状态
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else {
            // 远程调用失败
            // TODO 7，重复调用？ 接口幂等性；重试机制？
        }
    }

}