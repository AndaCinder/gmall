package com.lichen.gmall.service;

import com.lichen.gmall.vo.StockDelVo;

import java.util.List;

/**
 * @author 李琛
 * 2020/4/30 - 23:14
 */
public interface WareSkuService {
    void deleteByUserIds(List<StockDelVo> stockDelVos);
}
