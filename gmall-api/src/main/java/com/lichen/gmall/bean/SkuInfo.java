package com.lichen.gmall.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class SkuInfo implements Serializable {

  @Id
  @GeneratedValue(generator = "JDBC")//增加这个注解解决了通用Mapper的insert不返回主键的问题
  private String id;
  private String spuId;
  private BigDecimal price;
  private String skuName;
  private String skuDesc;
  private String weight;
  private String tmId;
  private String catalog3Id;
  private String skuDefaultImg;

  @Transient
  List<SkuImage> skuImageList;
  @Transient
  List<SkuAttrValue> skuAttrValueList;
  @Transient
  List<SkuSaleAttrValue> skuSaleAttrValueList;



}
