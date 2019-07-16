package com.lichen.gmall.manage.controller;

import com.lichen.gmall.bean.BaseAttrInfo;
import com.lichen.gmall.bean.BaseAttrValue;
import com.lichen.gmall.service.AttrService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author 李琛
 * 2019/7/10 - 9:04
 */
@RestController
public class AttrController {

    @Reference
    AttrService attrService;


    @RequestMapping("getAttrListByCtg3Id")
    public List<BaseAttrInfo> getAttrListByCtg3Id(String catalog3Id){
        return attrService.getAttrListByCtg3Id(catalog3Id);
    }

    @RequestMapping("getAttrList")
    public List<BaseAttrInfo> getAttrList(String catalog3Id){
        return attrService.getAttrList(catalog3Id);
    }

    /**
     * 编辑窗口弹出dialog时填充数据表格
     */
    @RequestMapping("getAttrValueList")
    public List<BaseAttrValue> getAttrValueList(String attrId){
        return attrService.getAttrValueList(attrId);
    }

    /**
     * 保存添加的数据
     */
    @RequestMapping("saveAttr")
    public String saveAttr(BaseAttrInfo baseAttrInfo){
        attrService.saveAttr(baseAttrInfo);
        return "success";
    }

    /**
     * 删除BaseAttrInfo和BaseAttrValue
     */
    @RequestMapping("deleteAttr")
    public String deleteAttr(String id){
        attrService.deleteAttr(id);
        return "success";
    }
}
