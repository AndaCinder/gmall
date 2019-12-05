package com.lichen.gmall.service;

import com.lichen.gmall.bean.*;

import java.util.List;
import java.util.Map;

/**
 * @author 李琛
 * 2019/7/11 - 9:47
 */
public interface SpuService {
    List<SpuInfo> getSpuList(String catalog3Id);

    List<BaseSaleAttr> baseSaleAttrList();

    void saveSpu(SpuInfo spuInfo);

    List<SpuImage> spuImageList(String spuId);

    List<SpuSaleAttr> spuSaleAttrList(String spuId);

    List<SpuSaleAttr> getSpuSaleAttrListBySpuId(String spuId);

    List<SpuSaleAttrValue> spuSaleAttrValueList(SpuSaleAttr spuSaleAttr);

    void deleteSpuInfo(String spuId);

    List<SpuImage> getSkuImageListBySpuId(String spuId);

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(String skuId,String spuId);

    List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);
}
