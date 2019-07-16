package com.lichen.gmall.manage.controller;

import com.lichen.gmall.bean.BaseAttrInfo;
import com.lichen.gmall.bean.SkuInfo;
import com.lichen.gmall.bean.SpuSaleAttr;
import com.lichen.gmall.service.AttrService;
import com.lichen.gmall.service.SkuService;
import com.lichen.gmall.service.SpuService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 李琛
 * 2019/7/15 - 10:06
 */
@RestController
public class SkuController {

    @Reference
    SkuService skuService;


    @RequestMapping("saveSku")
    public String saveSku(SkuInfo skuInfo){
        skuService.saveSku(skuInfo);
        return "success";
    }

    @RequestMapping("skuInfoListBySpu")
    public List<SkuInfo> skuInfoListBySpu(String spuId){
        return skuService.skuInfoListBySpu(spuId);
    }
}
