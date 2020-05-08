package com.lichen.gmall.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建统计页面的VO对象
 * @author 李琛
 * 2020/5/1 - 1:57
 */
@Data
@Accessors(chain = true)
public class StatVo implements Serializable {

    private List<String> skuNameList;
    private List<Integer> saleList;
    private List<String> modifiedTime;

    private List<SerialVo> serialList;

}
