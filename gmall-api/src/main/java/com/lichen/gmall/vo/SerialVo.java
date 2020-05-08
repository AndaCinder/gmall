package com.lichen.gmall.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 李琛
 * 2020/5/1 - 3:18
 */
@Data
@Accessors(chain = true)
public class SerialVo implements Serializable {
    private String name;
    private String type = "line" ;
    private String stack = "总量";
    private List<Integer> data;
}
