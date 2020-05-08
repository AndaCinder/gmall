package com.lichen.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lichen.gmall.service.StatisticsService;
import com.lichen.gmall.vo.SerialVo;
import com.lichen.gmall.vo.StatVo;
import com.lichen.gmall.bean.StatisticsDaily;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 李琛
 * 2020/5/1 - 1:16
 */
@RestController
@RequestMapping("stat")
public class StatisticsDailyController {

    @Reference
    StatisticsService statisticsService;

    private static final SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping("daily")
    public StatVo getDailyStatistics(){

        List<StatisticsDaily> dailies =  this.statisticsService.queryAll();

        StatVo statVo = new StatVo()
                .setSkuNameList(dailies.stream().map(StatisticsDaily::getSkuName).distinct().collect(Collectors.toList()))
                .setSaleList(dailies.stream().map(StatisticsDaily::getSalesNum).collect(Collectors.toList()))
                .setModifiedTime(
                        dailies.stream().map(daily -> ft.format(daily.getGmtModified())).distinct().collect(Collectors.toList())
                );
        List<SerialVo> serialVos = this.statisticsService.groupBySkuId(dailies);
        statVo.setSerialList(serialVos);
        return statVo;
    }

    @GetMapping("dataRandom")
    public String random(){
        this.statisticsService.insertRandom();
        return "success";
    }

}
