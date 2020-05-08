package com.lichen.gmall.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class SkuAttrValue implements Serializable {

  private String id;
  private String attrId;
  private String valueId;
  private String skuId;



}
