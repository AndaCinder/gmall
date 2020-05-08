package com.lichen.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lichen.gmall.bean.BaseAttrInfo;
import com.lichen.gmall.bean.SkuInfo;
import com.lichen.gmall.bean.SpuSaleAttr;
import com.lichen.gmall.service.AttrService;
import com.lichen.gmall.service.SkuService;
import com.lichen.gmall.service.SpuService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
        return skuService.getSkuListBySpuId(spuId);
    }

    @RequestMapping("deleteSkInfoBySkuId")
    public String deleteSkInfoBySpuId(String skuId){
        skuService.deleteSkInfoBySkuId(skuId);
        return "success";
    }

}
