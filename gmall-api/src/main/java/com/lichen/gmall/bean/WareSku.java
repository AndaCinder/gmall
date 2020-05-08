package com.lichen.gmall.bean;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WareSku {

  private long id;
  private long skuId;
  private long warehouseId;
  private long stock;
  private String stockName;
  private long stockLocked;



}
