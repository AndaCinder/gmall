package com.lichen.gmall.service;

import com.lichen.gmall.bean.SkuLsInfo;
import com.lichen.gmall.bean.SkuLsParam;

import java.util.List;

/**
 * @author 李琛
 * 2019/7/23 - 9:55
 */
public interface SearchService {
    List<SkuLsInfo> search(SkuLsParam skuLsParam);
}
