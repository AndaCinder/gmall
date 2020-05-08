package com.lichen.gmall.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author 李琛
 * 2020/5/1 - 0:12
 */
@Data
@Accessors(chain = true)
public class StockDelVo {
    private String skuId;
    private Integer skuNum;
}
