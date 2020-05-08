package com.lichen.gmall.manage.mapper;

import com.lichen.gmall.bean.BaseAttrInfo;
import com.lichen.gmall.bean.BaseAttrValue;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

public interface BaseAttrValueMapper extends Mapper<BaseAttrValue> {
    List<BaseAttrInfo> selectAttrListByValueIds(@Param("ids") Set<String> valueIds);
}
