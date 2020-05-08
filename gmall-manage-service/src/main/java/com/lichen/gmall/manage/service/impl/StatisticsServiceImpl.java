package com.lichen.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.lichen.gmall.manage.mapper.StatisticsMapper;
import com.lichen.gmall.service.StatisticsService;
import com.lichen.gmall.bean.StatisticsDaily;
import com.lichen.gmall.vo.SerialVo;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

/**
 * @author 李琛
 * 2020/5/1 - 1:48
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StatisticsMapper statisticsMapper;

    private static final SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public List<StatisticsDaily> queryAll() {
        return this.statisticsMapper.selectAll();
    }

    /**
     * 分组
     * @param dailies
     * @return
     */
    @Override
    public List<SerialVo> groupBySkuId(List<StatisticsDaily> dailies) {
        return dailies.stream().map(daily -> {
            SerialVo serialVo = new SerialVo();
            Example example = new Example(StatisticsDaily.class);
            example.createCriteria().andEqualTo("skuId",daily.getSkuId());
            List<StatisticsDaily> toolsList = this.statisticsMapper.selectByExample(example);
            serialVo.setName(daily.getSkuName())
                    .setData(toolsList.stream().map(StatisticsDaily::getSalesNum).collect(Collectors.toList()));
            return serialVo;
        }).collect(Collectors.toList());
    }












    @Override
    public void insertRandom(){
        LinkedList<StatisticsDaily> list = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            try {
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
                StatisticsDaily daily = new StatisticsDaily()
                        .setSkuId(new Integer(i + 7).longValue())
                        .setGmtCreated(ft.parse("2020-05-05" )).setGmtModified(ft.parse("2020-05-05" ));
                switch (i){
                    case 0:
                        daily.setSkuName("HUAWEIp30 64GB+128GB");
                        break;
                    case 1:
                        daily.setSkuName("HUAWEI p30 …128GB+256GB");
                        break;
                    case 2:
                        daily.setSkuName("iPhoneXR 天空灰版128GB");
                        break;
                    case 3:
                        daily.setSkuName("黑色款iPhone XR");
                        break;
                    case 4 :
                        daily.setSkuName("蓝色款iPhone XR 128GB");
                        break;
                    case 5:
                        daily.setSkuName("红色款iPhone XR 128GB");
                        break;
                    case 6:
                        daily.setSkuName("华为 HUAWEI P30 ");
                        break;
                    case 7:
                        daily.setSkuName("华为(HUAWEI) MateBook D");
                        break;
                    case 8:
                        daily.setSkuName("华为(HUAWEI) MateBook D");
                        break;
                    case 9:
                        daily.setSkuName("华为 HUAWEI P30 ");
                        break;
                }
                // 生成销量
                int nextInt = new Random().nextInt(1000);
                daily.setSalesNum(nextInt);
                list.add(daily);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        this.statisticsMapper.insertAll(list);
    }


}
