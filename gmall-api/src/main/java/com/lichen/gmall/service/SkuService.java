package com.lichen.gmall.service;

import com.lichen.gmall.bean.BaseAttrInfo;
import com.lichen.gmall.bean.SkuInfo;
import com.lichen.gmall.bean.SpuSaleAttr;

import java.util.List;

/**
 * @author 李琛
 * 2019/7/15 - 10:08
 */
public interface SkuService {
    List<SkuInfo> skuInfoListBySpu(String spuId);

    List<SpuSaleAttr> spuSaleAttrList(String spuId);


    void saveSku(SkuInfo skuInfo);
}
