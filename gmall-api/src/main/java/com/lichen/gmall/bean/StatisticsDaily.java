package com.lichen.gmall.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 李琛
 * 2020/5/1 - 1:45
 */
@Data
@Accessors(chain = true)
public class StatisticsDaily implements Serializable {

    private Integer id;
    private Long skuId;
    private String skuName;
    private Integer salesNum;
    private Date gmtCreated;
    private Date gmtModified;
}
