package com.lichen.gmall.manage.controller;

import com.lichen.gmall.bean.BaseCatalog1Id;
import com.lichen.gmall.service.JsonCatalogService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 李琛
 * 2019/11/26 - 20:05
 */
@RestController
public class JsonCatalogController {

    @Reference
    JsonCatalogService catalogService;

    @RequestMapping("getJSON")
    public List<BaseCatalog1Id> getJson(){
        List<BaseCatalog1Id> json = catalogService.getJson();
        return json;
    }

}
