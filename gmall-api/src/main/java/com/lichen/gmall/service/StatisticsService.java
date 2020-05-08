package com.lichen.gmall.service;

import com.lichen.gmall.bean.StatisticsDaily;
import com.lichen.gmall.vo.SerialVo;

import java.util.List;

/**
 * @author 李琛
 * 2020/5/1 - 1:47
 */
public interface StatisticsService {
    List<StatisticsDaily> queryAll();

    void insertRandom();

    List<SerialVo> groupBySkuId(List<StatisticsDaily> dailies);
}
