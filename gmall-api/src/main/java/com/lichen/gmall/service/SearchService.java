package com.lichen.gmall.service;

import com.lichen.gmall.bean.SkuLsInfo;
import com.lichen.gmall.bean.SkuLsParam;

import java.util.List;

public interface SearchService {

    List<SkuLsInfo> search(SkuLsParam skuLsParam);
}
