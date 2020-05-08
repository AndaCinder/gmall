package com.lichen.gmall.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class SkuLsParam implements Serializable{

    String  catalog3Id;

    String[] valueId;

    String keyword;

    int  pageNo=1;

    int pageSize=20;

}
