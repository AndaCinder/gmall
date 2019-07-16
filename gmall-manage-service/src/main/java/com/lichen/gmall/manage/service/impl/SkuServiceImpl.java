package com.lichen.gmall.manage.service.impl;

import com.lichen.gmall.bean.*;
import com.lichen.gmall.manage.mapper.*;
import com.lichen.gmall.service.SkuService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author 李琛
 * 2019/7/15 - 10:09
 */
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuImageMapper skuImageMapper;

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Override
    public List<SkuInfo> skuInfoListBySpu(String spuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSpuId(spuId);
        return skuInfoMapper.select(skuInfo);
    }

    /**
     * 查出数据并封装SpuSaleAttrValue进SpuSaleAttr
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> spuSaleAttrList(String spuId) {
        return null;
    }

    /**
     * 保存整体SkuInfo
     */
    @Override
    public void saveSku(SkuInfo skuInfo) {
        //编辑操作再打开删除功能
        if(skuInfo.getId()==null||skuInfo.getId().length()==0){
            skuInfo.setId(null);
            skuInfoMapper.insertSelective(skuInfo);
        }else {
            skuInfoMapper.updateByPrimaryKeySelective(skuInfo);
        }

//        //删除图片再插入
//        Example example = new Example(SkuImage.class);
//        example.createCriteria().andEqualTo("skuId",skuInfo.getId());
//        skuImageMapper.deleteByExample(example);

        //保存图片
        String skuId = skuInfo.getId();
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insertSelective(skuImage);
        }

//        //删除SkuAttrValue
//        Example skuAttrValueExample = new Example(SkuImage.class);
//        skuAttrValueExample.createCriteria().andEqualTo("skuId",skuInfo.getId());
//        skuAttrValueMapper.deleteByExample(skuAttrValueExample);

        //保存SkuAttrValue
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue attrValue : skuAttrValueList) {
            attrValue.setSkuId(skuId);
            skuAttrValueMapper.insertSelective(attrValue);
        }

//        //删除SkuSaleAttrValue
//        Example skuSaleAttrValueExample = new Example(SkuImage.class);
//        skuSaleAttrValueExample.createCriteria().andEqualTo("skuId",skuInfo.getId());
//        skuSaleAttrValueMapper.deleteByExample(skuSaleAttrValueExample);

        //保存SkuSaleAttrValue
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue saleAttrValue : skuSaleAttrValueList) {
            saleAttrValue.setSkuId(skuId);
            skuSaleAttrValueMapper.insertSelective(saleAttrValue);
        }
    }
}
