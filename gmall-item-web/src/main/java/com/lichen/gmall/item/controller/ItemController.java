package com.lichen.gmall.item.controller;

import com.alibaba.fastjson.JSON;
import com.lichen.gmall.bean.SkuInfo;
import com.lichen.gmall.bean.SkuSaleAttrValue;
import com.lichen.gmall.bean.SpuSaleAttr;
import com.lichen.gmall.bean.UserInfo;
import com.lichen.gmall.service.SkuService;
import com.lichen.gmall.service.SpuService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 李琛
 * 2019/7/16 - 16:22
 */
@Controller
public class ItemController {

    @Reference
    SkuService skuService;

    @Reference
    SpuService spuService;

    @RequestMapping("/{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap map){

        SkuInfo skuInfo = skuService.getSkuById(skuId);

        map.put("skuInfo",skuInfo);

        String spuId = skuInfo.getSpuId();

        //销售属性列表
        List<SpuSaleAttr> saleAttrListCheckBySku = spuService.getSpuSaleAttrListCheckBySku(skuInfo.getId(),spuId);

        map.put("spuSaleAttrListCheckBySku",saleAttrListCheckBySku);

        //根据spuId查出的有关的所有skuInfo及他们的SkuSaleAttrValue
        List<SkuSaleAttrValue> attrValueListBySpu = spuService.getSkuSaleAttrValueListBySpu(spuId);
        String valueIdsKey="";

        Map<String,String> valuesSkuMap=new HashMap<>();

        for (int i = 0; i < attrValueListBySpu.size(); i++) {
            SkuSaleAttrValue skuSaleAttrValue = attrValueListBySpu.get(i);
            if(valueIdsKey.length()!=0){
                valueIdsKey= valueIdsKey+"|";
            }
            valueIdsKey=valueIdsKey+skuSaleAttrValue.getSaleAttrValueId();

            if((i+1)== attrValueListBySpu.size()||!skuSaleAttrValue.getSkuId().equals(attrValueListBySpu.get(i+1).getSkuId())  ){

                valuesSkuMap.put(valueIdsKey,skuSaleAttrValue.getSkuId());
                valueIdsKey="";
            }

        }

        //把map变成json串
        String valuesSkuJson = JSON.toJSONString(valuesSkuMap);

        map.put("valuesSkuJson",valuesSkuJson);
        return "item";
    }



    @RequestMapping("index")
    public String index(ModelMap map){
        map.put("hello","hello Thymeleaf");

        List<UserInfo> userInfos = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setNickName("老"+i);
            userInfo.setPhoneNum("15478965"+i);
            userInfos.add(userInfo);
        }
        map.put("userInfos",userInfos);
        return "demo";
    }
}
