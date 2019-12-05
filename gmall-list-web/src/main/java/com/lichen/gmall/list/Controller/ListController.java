package com.lichen.gmall.list.Controller;

import com.lichen.gmall.bean.*;
import com.lichen.gmall.service.AttrService;
import com.lichen.gmall.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * @author 李琛
 * 2019/7/18 - 13:50
 */
@Controller
public class ListController {

    @Reference
    SearchService searchService;

    @Reference
    AttrService attrService;

    @RequestMapping("list.html")
    public String search(SkuLsParam skuLsParam, ModelMap map){
        List<SkuLsInfo> skuLsInfoList = searchService.search(skuLsParam);

        //封装平台属性列表，
        List<BaseAttrInfo> baseAttrInfos = getAttrValueId(skuLsInfoList);

        // 这次请求参数过来携带的valueId
        String[] valueId = skuLsParam.getValueId();

        // 同步请求，参数拼接，包含valueId catalogId keyword
        String urlParam = getUrlParam(skuLsParam);

        List<Crumb> crumbs = new ArrayList<>();
        /**
         * 当点击一行平台属性，则这一行被去掉
         */
        if (valueId != null && valueId.length > 0){//当有valueId被选中时，开始删除操作
            Iterator<BaseAttrInfo> iterator = baseAttrInfos.iterator();
            while (iterator.hasNext()){
                BaseAttrInfo baseAttrInfo = iterator.next();
                List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
                for (BaseAttrValue baseAttrValue : attrValueList) {
                    for (String valId : valueId) {
                        if (baseAttrValue.getId().equals(valId)) {//这一行说明选择了当前属性
                            crumbs.add(makeCrumb(skuLsParam,baseAttrInfos, urlParam,valId));
                            iterator.remove();//当条件满足，查出满足的BaseAttrInfo
                        }
                    }
                }
            }
        }

        map.put("urlParam",urlParam);
        map.put("attrValueSelectedList",crumbs);//属性列表面包屑
        map.put("attrList",baseAttrInfos);
        map.put("skuLsInfoList",skuLsInfoList);
        return "list";
    }

    /**
     * 面包屑的制作
     * @param valId         现在选中的id值
     * @param baseAttrInfos
     * @param urlParam  catalog3Id=1&valueId=3&valueId=4&valueId=9
     * 需要遍历baseAttrInfos，然后根据选择的valueId来排除valueId=? 然后获得valueId名
     */
    private Crumb makeCrumb(SkuLsParam skuLsParam,List<BaseAttrInfo> baseAttrInfos,String urlParam,String valId) {
        String[] valueId = skuLsParam.getValueId();
        String[] splits = urlParam.split("&");
        if (valueId!=null && valueId.length >0) {

            for (BaseAttrInfo baseAttrInfo : baseAttrInfos) {
                List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
                for (BaseAttrValue attrValue : attrValueList) {
                    for (String s : valueId) {
                        if (attrValue.getId().equals(s)) {
                            Crumb crumb = new Crumb();
                            String urlParamForCrumb = splits[0];
                            for (int i = 1; i < splits.length; i++) {
                                if (splits[i].contains(s) && s.equals(valId) && !splits[i].contains("keyword") && !splits[i].contains("catalog3Id")) {
                                    splits[i] = "";
                                }
                                if (!"".equals(splits[i])) {
                                    urlParamForCrumb += "&" + splits[i];
                                }
                            }
                            crumb.setUrlParam(urlParamForCrumb);
                            String valueNameForCrumb = attrValue.getValueName();
                            crumb.setValueName(valueNameForCrumb);
                            return crumb;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 对用户选择的属性进行参数拼接?catalog3Id=1&valueId=1&valueId=2&keyword=手机
     * @param skuLsParam
     * @return
     */
    private String getUrlParam(SkuLsParam skuLsParam) {
        String catalog3Id = skuLsParam.getCatalog3Id();
        String[] valueId = skuLsParam.getValueId();
        String keyword = skuLsParam.getKeyword();
        String urlParam = "";
        /**
         * 说明拼接urlParm参数由三个部分组成，中间由&想连接参数
         * 如果urlParam为空，则开头不需要&
         * 因为是get请求所有存在keyword和catalog3Id都同时存在的情况
         */
        if (StringUtils.isNotBlank(catalog3Id)){
            if (StringUtils.isBlank(urlParam))
                urlParam = "catalog3Id=" + catalog3Id;
            else
                urlParam = urlParam + "&catalog3Id=" + catalog3Id;
        }

        if (StringUtils.isNotBlank(keyword)){
            if (StringUtils.isBlank(urlParam))
                urlParam = "keyword=" + keyword;
            else
                urlParam = urlParam + "&keyword" + keyword;
        }
        /*
            遍历valueId 然后以&valueId=形式进入Get参数中
        */
        if (valueId != null && valueId.length > 0){
            for (String s : valueId) {
                urlParam = urlParam + "&valueId=" + s;
            }
        }

        return urlParam;
    }

    /**
     * 根据SkuLsInfo中的skuAttrValueList.valueId 来获取平台属性BaseAttrInfo
     * @param skuLsInfoList
     * @return
     */
    private List<BaseAttrInfo> getAttrValueId(List<SkuLsInfo> skuLsInfoList) {
        Set<String> valueIds = new HashSet<>();

        for (SkuLsInfo skuLsInfo : skuLsInfoList) {
            List<SkuLsAttrValue> skuAttrValueList = skuLsInfo.getSkuAttrValueList();
            for (SkuLsAttrValue skuLsAttrValue : skuAttrValueList) {
                valueIds.add(skuLsAttrValue.getValueId());
            }

        }
        List<BaseAttrInfo> baseAttrInfos = attrService.getAttrListByValueIds(valueIds);

        return baseAttrInfos;
    }


    @RequestMapping("index")
    public String index(){
        return "index";
    }
}
