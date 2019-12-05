package com.lichen.gmall.manage.mapper;

import com.lichen.gmall.bean.BaseAttrInfo;
import com.lichen.gmall.bean.BaseAttrValue;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author 李琛
 * 2019/7/10 - 10:36
 */
public interface BaseAttrValueMapper extends Mapper<BaseAttrValue> {

    List<BaseAttrInfo> selectAttrListByValueIds(@Param("ids") String join);
}
