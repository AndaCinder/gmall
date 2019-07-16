package com.lichen.gmall.manage.controller;

import com.lichen.gmall.bean.*;
import com.lichen.gmall.service.AttrService;
import com.lichen.gmall.service.SpuService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 李琛
 * 2019/7/10 - 9:04
 */
@RestController
public class SpuController {

    @Reference
    SpuService spuService;

    /**
     * 获得Sku添加列表中所需要的SpuImage数据
     */
    @RequestMapping("getSkuImageListBySpuId")
    public List<SpuImage> getSkuImageListBySpuId(String spuId){
        return spuService.getSkuImageListBySpuId(spuId);
    }

    /**
     * sku添加列表中Spu属性和Spu属性值的获取
     * @param spuId
     * @return
     */
    @RequestMapping("getSpuSaleAttrListBySpuId")
    public List<SpuSaleAttr> getSpuSaleAttrListBySpuId(String spuId){
        return spuService.getSpuSaleAttrListBySpuId(spuId);
    }

    /**
     * 保存SpuInfo
     */
    @RequestMapping("saveSpu")
    public String saveSpu(SpuInfo spuInfo){
        spuService.saveSpu(spuInfo);
        return "success";
    }

    /**
     * 编辑回显imgdatagrid数据
     */
    @RequestMapping("spuImageList")
    public List<SpuImage> spuImageList(String spuId){
        return spuService.spuImageList(spuId);
    }

    /**
     * 编辑回显SpuSaleAttr表格数据
     */
    @RequestMapping("spuSaleAttrList")
    public List<SpuSaleAttr> spuSaleAttrList(String spuId){
        List<SpuSaleAttr> spuSaleAttrs = spuService.spuSaleAttrList(spuId);
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrs) {
            List<SpuSaleAttrValue> spuSaleAttrValues = spuService.spuSaleAttrValueList(spuSaleAttr);
            Map map = new HashMap<>();
            map.put("total",spuSaleAttrs.size());
            map.put("rows",spuSaleAttrValues);
            spuSaleAttr.setSpuSaleAttrValueJson(map);
        }
        return spuSaleAttrs;
    }

    @RequestMapping("spuList")
    public List<SpuInfo> getSpuList(String catalog3Id){
        return spuService.getSpuList(catalog3Id);
    }


    @RequestMapping("baseSaleAttrList")
    public List<BaseSaleAttr> baseSaleAttrList(){
        return spuService.baseSaleAttrList();
    }

    @RequestMapping("deleteSpuInfo")
    public String deleteSpuInfo(String spuId){
        spuService.deleteSpuInfo(spuId);
        return "success";
    }
}
