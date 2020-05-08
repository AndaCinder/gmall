package com.lichen.gmall.bean;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CartInfo implements Serializable {

  private String id;
  private String userId;
  private String skuId;
  private BigDecimal cartPrice;
  private long quantity;
  private String imgUrl;
  private String isChecked;
  private BigDecimal skuPrice;
  private Integer skuNum;
  private String skuName;

}
