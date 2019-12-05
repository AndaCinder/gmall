package com.lichen.gmall.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.lichen.gmall.bean.*;
import com.lichen.gmall.manage.mapper.*;
import com.lichen.gmall.service.SkuService;
import com.lichen.gmall.util.Redis1Util;
import com.lichen.gmall.util.Redis2Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.Collections;
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

    @Autowired
    Redis1Util redis1Util;

    @Autowired
    Redis2Util redis2Util;

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
     * =========================
     * version2 19/12/3
     * 更新skuInfo编辑功能，先删除再插入
     * 需要更新ElasticSearch的索引
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

        //删除图片再插入
        Example example = new Example(SkuImage.class);
        example.createCriteria().andEqualTo("skuId",skuInfo.getId());
        skuImageMapper.deleteByExample(example);


        //保存图片
        String skuId = skuInfo.getId();
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            if (skuImage.getId() != null && skuImage.getId().length()==0)
                skuImage.setId(null);
            skuImageMapper.insertSelective(skuImage);
        }

        //删除SkuAttrValue
        Example skuAttrValueExample = new Example(SkuAttrValue.class);
        skuAttrValueExample.createCriteria().andEqualTo("skuId",skuInfo.getId());
        skuAttrValueMapper.deleteByExample(skuAttrValueExample);

        //保存SkuAttrValue
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue attrValue : skuAttrValueList) {
            attrValue.setSkuId(skuId);
            if (attrValue.getId() != null && attrValue.getId().length() ==0)
                attrValue.setId(null);
            skuAttrValueMapper.insertSelective(attrValue);
        }

        //删除SkuSaleAttrValue
        Example skuSaleAttrValueExample = new Example(SkuSaleAttrValue.class);
        skuSaleAttrValueExample.createCriteria().andEqualTo("skuId",skuInfo.getId());
        skuSaleAttrValueMapper.deleteByExample(skuSaleAttrValueExample);

        //保存SkuSaleAttrValue
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue saleAttrValue : skuSaleAttrValueList) {
            saleAttrValue.setSkuId(skuId);
            saleAttrValue.setId(null);
            skuSaleAttrValueMapper.insertSelective(saleAttrValue);
        }
    }

    /**
     * item模块中查询单个skuInfo包括其图片属性 销售属性 实现分布式锁
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuById(String skuId) {
        Jedis jedis = redis1Util.getJedis();
        SkuInfo skuInfo = null;
        //查询redis缓存
        String key = "sku:"+skuId + ":info";
        String val = jedis.get(key);

        if ("empty".equals(val)) {
            System.err.println(Thread.currentThread().getName() + "发现数据库中暂时没有该商品，返回空对象");
            return skuInfo;
        }

        if (skuInfo == null){
            //申请缓存锁
            Jedis jedis2 = redis2Util.getJedis2();
            String ok = jedis2.set("sku:" + skuId + ":lock", "1", "nx", "px", 3000);

            if (StringUtils.isNotBlank(ok)){//拿到锁
                System.out.println(Thread.currentThread().getName() + "获得分布式锁，开始访问数据库");
                String value = jedis2.get("sku:" + skuId + ":lock");
                try {
                    //查询数据库
                    skuInfo = getSkuByIdFromDb(skuId);
                    if (skuInfo != null){
                        //同步缓存
                        jedis.set(key,JSON.toJSONString(skuInfo));
                    }else{
                        //通知其他线程没有该数据
                        jedis2.setex("sku:"+skuId + ":info",10,"empty");
                    }
                }finally {
                    System.out.println(Thread.currentThread().getName() + "归还分布式锁");
                    String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    //归还缓存锁
                    jedis2.eval(luaScript,Collections.singletonList("sku:" + skuId + ":lock"),Collections.singletonList(value));
                }

            }else {//排队等待
                //自旋
                System.out.println(Thread.currentThread().getName() + "获得分布式锁失败，开始自旋");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String ok2 ="";
                do {
                    ok2 = jedis2.set("sku:" + skuId + ":lock", "1", "nx", "px", 300000);
                }while (StringUtils.isNotBlank(ok2));
            }
        }else {
            //正常转化缓存数据
            System.out.println(Thread.currentThread().getName() + "获得数据成功");
            skuInfo = JSON.parseObject(val,SkuInfo.class);
        }

        return skuInfo;
    }

    /**
     * 查出SkuInfo封装入ElasticSearch
     * @param catalog3Id
     * @return 返回封装了SkuAttrValueList（平台属性）的SkuInfo
     */
    @Override
    public List<SkuInfo> getSkuListByCatalog3Id(String catalog3Id) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setCatalog3Id(catalog3Id);
        List<SkuInfo> select = skuInfoMapper.select(skuInfo);

        for (SkuInfo info : select) {
            String id = info.getId();

            SkuAttrValue skuAttrValue = new SkuAttrValue();
            skuAttrValue.setSkuId(id);
            List<SkuAttrValue> attrValues = skuAttrValueMapper.select(skuAttrValue);

            info.setSkuAttrValueList(attrValues);
        }
        return select;
    }

    /**
     * 删除Sku列表中的Sku信息
     * @param skuId
     */
    @Override
    public void deleteSkInfoBySkuId(String skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSpuId(skuId);
        skuInfoMapper.deleteByPrimaryKey(skuInfo);
    }





    /**
     * 根据SkuId查询数据库数据
     * @param skuId
     * @return 返回 SkuInfo并且封装了两个Transient的
     * skuImageList（图片）
     * skuSaleAttrValueList（销售属性）
     * 然后返回
     */
    private SkuInfo getSkuByIdFromDb(String skuId){
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        SkuInfo skuInfo1 = skuInfoMapper.selectOne(skuInfo);

        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImages = skuImageMapper.select(skuImage);
        skuInfo1.setSkuImageList(skuImages);

        SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuId);
        List<SkuSaleAttrValue> skuSaleAttrValues = skuSaleAttrValueMapper.select(skuSaleAttrValue);
        skuInfo1.setSkuSaleAttrValueList(skuSaleAttrValues);
        return skuInfo1;
    }
}
