package com.lichen.gmall.manage.mapper;

import com.lichen.gmall.bean.StatisticsDaily;
import tk.mybatis.mapper.common.Mapper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author 李琛
 * 2020/5/1 - 1:48
 */
public interface StatisticsMapper extends Mapper<StatisticsDaily> {
    void insertAll(List<StatisticsDaily> list);
}
