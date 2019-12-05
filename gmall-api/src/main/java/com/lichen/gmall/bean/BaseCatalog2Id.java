package com.lichen.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * @author 李琛
 * 2019/11/26 - 19:59
 */
@Table(name = "base_catalog2")
public class BaseCatalog2Id implements Serializable {

    @Column
    private String catalog1Id;
    @Transient
    List<BaseCatalog3Id> catalog3List;

    @Id
    @Column
    private String id;

    @Column
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCatalog1Id() {
        return catalog1Id;
    }

    public void setCatalog1Id(String catalog1Id) {
        this.catalog1Id = catalog1Id;
    }

    public List<BaseCatalog3Id> getCatalog3List() {
        return catalog3List;
    }

    public void setCatalog3List(List<BaseCatalog3Id> catalog3List) {
        this.catalog3List = catalog3List;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
