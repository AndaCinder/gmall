package com.lichen.gmall.manage.service.impl;

import com.lichen.gmall.bean.BaseCatalog1Id;
import com.lichen.gmall.bean.BaseCatalog2Id;
import com.lichen.gmall.bean.BaseCatalog3Id;
import com.lichen.gmall.manage.mapper.Basecatalog1IdMapper;
import com.lichen.gmall.manage.mapper.Basecatalog2IdMapper;
import com.lichen.gmall.manage.mapper.Basecatalog3IdMapper;
import com.lichen.gmall.service.JsonCatalogService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author 李琛
 * 2019/11/26 - 20:18
 */
@Service
public class JsonCatalogServiceImpl implements JsonCatalogService {

    @Autowired
    Basecatalog1IdMapper catalog1Mapper;

    @Autowired
    Basecatalog2IdMapper catalog2Mapper;

    @Autowired
    Basecatalog3IdMapper catalog3IdMapper;

    @Override
    public List<BaseCatalog1Id> getJson(){
        List<BaseCatalog1Id> catalog1Ids = catalog1Mapper.selectAll();
        for (BaseCatalog1Id catalog1Id : catalog1Ids) {
            BaseCatalog2Id baseCatalog2Id = new BaseCatalog2Id();
            baseCatalog2Id.setCatalog1Id(catalog1Id.getId());
            List<BaseCatalog2Id> catalog2Ids = catalog2Mapper.select(baseCatalog2Id);
            for (BaseCatalog2Id catalog2Id : catalog2Ids) {
                BaseCatalog3Id baseCatalog3Id = new BaseCatalog3Id();
                baseCatalog3Id.setCatalog2Id(catalog2Id.getId());
                catalog2Id.setCatalog3List(catalog3IdMapper.select(baseCatalog3Id));
            }
            catalog1Id.setBaseCatalog2IdList(catalog2Ids);
        }
        return catalog1Ids;
    }

    /*//生成Json文件
    public List<BaseCatalog1> getCatalogJson(){
        List<BaseCatalog1> catalog1s = catalog1Mapper.selectAll();
        for (BaseCatalog1 catalog1 : catalog1s) {
            BaseCatalog2 baseCatalog2 = new BaseCatalog2();
            baseCatalog2.setCatalog1Id(catalog1.getId());
            List<BaseCatalog2> catalog2s = catalog2Mapper.select(baseCatalog2);
            for (BaseCatalog2 catalog2 : catalog2s) {
                BaseCatalog3 baseCatalog3 = new BaseCatalog3();
                baseCatalog3.setCatalog2Id(catalog2.getId());
                List<BaseCatalog3> catalog3s = catalog3Mapper.select(baseCatalog3);
                catalog2.setBaseCatalog3(catalog3s);
            }
            catalog1.setBaseCatalog2(catalog2s);
        }
        return catalog1s;
    }*/
}
