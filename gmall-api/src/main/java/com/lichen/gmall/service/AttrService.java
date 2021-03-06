package com.lichen.gmall.service;

import com.lichen.gmall.bean.BaseAttrInfo;
import com.lichen.gmall.bean.BaseAttrValue;

import java.util.List;
import java.util.Set;

public interface AttrService {

    List<BaseAttrInfo> getAttrList(String catalog3Id);

    void saveAttr(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValue(String attrName);

    void deleteAttr(String attrId);

    List<BaseAttrInfo> getAttrListByCtg3Id(String ctg3Id);

    List<BaseAttrInfo> getAttrListByValueIds(Set<String> valueIds);
}
