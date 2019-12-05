package com.lichen.gmall.service;

import com.lichen.gmall.bean.BaseCatalog1;
import com.lichen.gmall.bean.BaseCatalog2;
import com.lichen.gmall.bean.BaseCatalog3;

import java.util.List;

/**
 * @author 李琛
 * 2019/7/9 - 17:54
 */
public interface CatalogService {
    List<BaseCatalog1> getCatalog1();

    List<BaseCatalog2> getCatalog2(String catalog1Id);

    List<BaseCatalog3> getCatalog3(String catalog2Id);

//    //临时
//    List<BaseCatalog1> getCatalogJson();
}
