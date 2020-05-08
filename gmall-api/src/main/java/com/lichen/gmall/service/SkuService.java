package com.lichen.gmall.service;


import com.lichen.gmall.bean.SkuInfo;

import java.math.BigDecimal;
import java.util.List;

public interface SkuService {

    List<SkuInfo> getSkuListBySpuId(String spuId);

    void saveSku(SkuInfo skuInfo);

    SkuInfo getSkuById(String skuId);

    List<SkuInfo> getSkuListByCatalog3Id(String catalog3Id);

    boolean checkPrice(BigDecimal skuPrice, String skuId);

    void deleteSkInfoBySkuId(String skuId);
}
