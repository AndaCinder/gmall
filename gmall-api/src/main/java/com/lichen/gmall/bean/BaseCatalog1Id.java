package com.lichen.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * 临时生成的Basecatalog1 的代替Bean 为了生成页面需要的JSON
 * @author 李琛
 * 2019/11/26 - 19:56
 */
@Table(name = "base_catalog1")
public class BaseCatalog1Id implements Serializable {
    @Id
    @Column
    private String id;

    @Transient
    List<BaseCatalog2Id> baseCatalog2IdList;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<BaseCatalog2Id> getBaseCatalog2IdList() {
        return baseCatalog2IdList;
    }

    public void setBaseCatalog2IdList(List<BaseCatalog2Id> baseCatalog2IdList) {
        this.baseCatalog2IdList = baseCatalog2IdList;
    }
}
