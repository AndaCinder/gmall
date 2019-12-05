package com.lichen.gmall.manage.mapper;

import com.lichen.gmall.bean.SkuSaleAttrValue;
import com.lichen.gmall.bean.SpuSaleAttr;
import com.lichen.gmall.bean.SpuSaleAttrValue;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author 李琛
 * 2019/7/11 - 21:30
 */
@org.apache.ibatis.annotations.Mapper
public interface SpuSaleAttrValueMapper extends Mapper<SpuSaleAttrValue> {

     List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(String skuId,String spuId);

     List<SkuSaleAttrValue> selectSkuSaleAttrValueListBySpu(@Param("spuId") String spuId);
}
