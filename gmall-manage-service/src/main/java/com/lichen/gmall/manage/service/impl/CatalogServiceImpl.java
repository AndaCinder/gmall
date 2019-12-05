package com.lichen.gmall.manage.service.impl;

import com.lichen.gmall.bean.BaseCatalog1;
import com.lichen.gmall.bean.BaseCatalog2;
import com.lichen.gmall.bean.BaseCatalog3;
import com.lichen.gmall.manage.mapper.Catalog1Mapper;
import com.lichen.gmall.manage.mapper.Catalog2Mapper;
import com.lichen.gmall.manage.mapper.Catalog3Mapper;
import com.lichen.gmall.service.CatalogService;
import org.apache.dubbo.config.annotation.Service;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author 李琛
 * 2019/7/9 - 18:00
 */
@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    Catalog1Mapper catalog1Mapper;
    @Autowired
    Catalog2Mapper catalog2Mapper;
    @Autowired
    Catalog3Mapper catalog3Mapper;

    @Override
    public List<BaseCatalog1> getCatalog1() {
        return catalog1Mapper.selectAll();
    }


    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);
        return catalog2Mapper.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);
        return catalog3Mapper.select(baseCatalog3);
    }
}
