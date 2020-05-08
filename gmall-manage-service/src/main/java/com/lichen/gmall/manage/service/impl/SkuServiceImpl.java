package com.lichen.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.lichen.gmall.annotation.GmallCache;
import com.lichen.gmall.bean.SkuAttrValue;
import com.lichen.gmall.bean.SkuImage;
import com.lichen.gmall.bean.SkuInfo;
import com.lichen.gmall.bean.SkuSaleAttrValue;
import com.lichen.gmall.manage.mapper.SkuAttrValueMapper;
import com.lichen.gmall.manage.mapper.SkuImageMapper;
import com.lichen.gmall.manage.mapper.SkuInfoMapper;
import com.lichen.gmall.manage.mapper.SkuSaleAttrValueMapper;
import com.lichen.gmall.service.SkuService;
import com.lichen.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    SkuInfoMapper skuInfoMapper;
    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    SkuImageMapper skuImageMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<SkuInfo> getSkuListBySpuId(String spuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSpuId(spuId);
        return skuInfoMapper.select(skuInfo);
    }

    @Override
    public void saveSku(SkuInfo skuInfo) {
        skuInfoMapper.insert(skuInfo);
        String skuId = skuInfo.getId();

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue :
                skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.insert(skuAttrValue);
        }

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue :
                skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }

        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage :
                skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insert(skuImage);
        }

    }

    /**
     * 加分布式锁
     * 通过AOP的方式
     * @param skuId
     * @return
     */
    @Override
    @GmallCache(prefix = "manage:skuids",lock = "locks:",random = 10,timeout = 1440)
    public SkuInfo getSkuById(String skuId) {
        //查询数db
        return  getSkuByIdFormDb(skuId);
    }

    @Override
    public List<SkuInfo> getSkuListByCatalog3Id(String catalog3Id) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setCatalog3Id(catalog3Id);
        List<SkuInfo> skuInfos = skuInfoMapper.select(skuInfo);
        skuInfos.parallelStream().forEach(sku -> {
            SkuAttrValue skuAttrValue = new SkuAttrValue().setSkuId(sku.getId());
            List<SkuAttrValue> skuAttrValues = this.skuAttrValueMapper.select(skuAttrValue);
            sku.setSkuAttrValueList(skuAttrValues);
        });

        return skuInfos;
    }

    @Override
    public boolean checkPrice(BigDecimal skuPrice, String skuId) {
        boolean bPrice = false;
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        SkuInfo sku = skuInfoMapper.selectOne(skuInfo);
        if (sku != null) {
            int i = sku.getPrice().compareTo(skuPrice);
            if (i == 0) {
                bPrice = true;
            }
        }

        return bPrice;
    }

    /**
     * 删除sku
     * @param skuId
     */
    @Override
    public void deleteSkInfoBySkuId(String skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSpuId(skuId);
        skuInfoMapper.deleteByPrimaryKey(skuInfo);
    }

    private SkuInfo getSkuByIdFormDb(String skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        SkuInfo sku = skuInfoMapper.selectOne(skuInfo);

        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImages = skuImageMapper.select(skuImage);
        sku.setSkuImageList(skuImages);

        SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuId);
        List<SkuSaleAttrValue> skuSaleAttrValues = skuSaleAttrValueMapper.select(skuSaleAttrValue);
        sku.setSkuSaleAttrValueList(skuSaleAttrValues);
        return sku;
    }


}
