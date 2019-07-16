package com.lichen.gmall.service;

import com.lichen.gmall.bean.BaseAttrInfo;
import com.lichen.gmall.bean.BaseAttrValue;

import java.util.List;

/**
 * @author 李琛
 * 2019/7/10 - 8:51
 */
public interface AttrService {
    List<BaseAttrInfo> getAttrList(String catalog3Id);

    List<BaseAttrValue> getAttrValueList(String attrId);

    void saveAttr(BaseAttrInfo baseAttrInfo);

    void deleteAttr(String id);

    List<BaseAttrInfo> getAttrListByCtg3Id(String catalog3Id);
}
