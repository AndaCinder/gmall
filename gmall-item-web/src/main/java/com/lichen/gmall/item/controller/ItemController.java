package com.lichen.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.lichen.gmall.bean.SkuInfo;
import com.lichen.gmall.bean.SkuSaleAttrValue;
import com.lichen.gmall.bean.SpuSaleAttr;
import com.lichen.gmall.bean.UserInfo;
import com.lichen.gmall.service.SkuService;
import com.lichen.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Controller
public class ItemController {

    @Reference
    SkuService skuService;

    @Reference
    SpuService spuService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @RequestMapping("index")
    public String index(ModelMap map) {
        map.put("hello", "hello,thymeleaf");

        List<UserInfo> userInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setNickName("小" + i);
            userInfo.setPhoneNum("111111");
            userInfos.add(userInfo);
        }
        map.put("userInfos", userInfos);

        return "demo";
    }

    /**
     * 这边做上异步编排
     * @param skuId
     * @param map
     * @return
     */
    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap map) {
        // 优化效率
        //查询并返回sku
        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = skuService.getSkuById(skuId);
            map.put("skuInfo", skuInfo);
            return skuInfo;
        }, threadPoolExecutor);

        CompletableFuture<Void> saleAttrValueFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            //查询并返回销售属性列表和销售属性值列表，并通过设置isCheck属性把页面上skuId对应的销售属性值标红
            Map<String, String> idMap = new HashMap<>();
            idMap.put("spuId", skuInfo.getSpuId());
            idMap.put("skuId", skuId);
            List<SpuSaleAttr> saleAttrListBySpuId = spuService.getSpuSaleAttrListCheckBySku(idMap);
            map.put("spuSaleAttrListCheckBySku", saleAttrListBySpuId);
        }, threadPoolExecutor);

        CompletableFuture<Void> skuJsonFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            //将当前sku对应的兄弟sku列表所对应的销售属性值列表组合成map，然后转化为Json返回给前台,用于点击销售属性值时页面间的切换
            List<SkuInfo> skuInfos = spuService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
            Map<String, String> map1 = new HashMap<>();
            for (SkuInfo skuInfo1 :
                    skuInfos) {
                String value = skuInfo1.getId();
                String key = "";
                List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo1.getSkuSaleAttrValueList();
                for (SkuSaleAttrValue saleAttrValue :
                        skuSaleAttrValueList) {
                    key = key + "|" + saleAttrValue.getSaleAttrValueId();
                }
                map1.put(key, value);
            }
            String skuJson = JSON.toJSONString(map1);
            map.put("skuJson", skuJson);
        }, threadPoolExecutor);

        CompletableFuture.allOf(skuInfoFuture,saleAttrValueFuture,skuJsonFuture).join();
        return "item";
    }

}















