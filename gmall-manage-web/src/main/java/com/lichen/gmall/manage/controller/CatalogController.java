package com.lichen.gmall.manage.controller;

import com.lichen.gmall.bean.BaseCatalog1;
import com.lichen.gmall.bean.BaseCatalog2;
import com.lichen.gmall.bean.BaseCatalog3;
import com.lichen.gmall.service.CatalogService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 李琛
 * 2019/7/9 - 17:52
 */
@RestController
public class CatalogController {

    @Reference
    CatalogService catalogService;



    @RequestMapping("getCatalog1")
    public List<BaseCatalog1> getCatalog1(){
        return catalogService.getCatalog1();
    }


    @RequestMapping("getCatalog2")
    public List<BaseCatalog2> getCatalog2(String catalog1Id){
        return catalogService.getCatalog2(catalog1Id);
    }

    @RequestMapping("getCatalog3")
    public List<BaseCatalog3> getCatalog3(String catalog2Id){
        return catalogService.getCatalog3(catalog2Id);
    }


}
