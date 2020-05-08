package com.lichen.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.lichen.gmall.bean.CartInfo;
import com.lichen.gmall.cart.mapper.CartInfoMapper;
import com.lichen.gmall.service.CartService;
import com.lichen.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartInfoMapper cartInfoMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public CartInfo ifCartExist(CartInfo cartInfo) {

        boolean ifCartExist = false;

        // 缓存查询一下
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps("carts:" + cartInfo.getUserId() + ":info");
        String json = (String) hashOps.get(cartInfo.getSkuId());
        if (StringUtils.isNotBlank(json))
            return JSON.parseObject(json,CartInfo.class);


        CartInfo info = new CartInfo();
        info.setUserId(cartInfo.getUserId());
        info.setSkuId(cartInfo.getSkuId());
        return cartInfoMapper.selectOne(info);
    }

    @Override
    public void updateCart(CartInfo cartInfoDb) {
        Example example = new Example(CartInfo.class);
        example.createCriteria().andEqualTo("userId",cartInfoDb.getUserId()).andEqualTo("skuId",cartInfoDb.getSkuId());
        cartInfoMapper.updateByExampleSelective(cartInfoDb,example);
    }

    @Override
    public void insertCart(CartInfo cartInfo) {
        cartInfoMapper.insertSelective(cartInfo);
    }

    /**
     * 购物车同步模块
     * 缓存同步
     * @param userId
     */
    @Override
    public void syncCache(String userId) {
        Jedis jedis = redisUtil.getJedis();

        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        List<CartInfo> cartInfos = cartInfoMapper.select(cartInfo);

        Map<String, String> cartInfoMap = new HashMap<>();
        for (CartInfo info : cartInfos) {
            cartInfoMap.put(info.getSkuId(), JSON.toJSONString(info));
        }
        if (cartInfoMap != null && cartInfoMap.size() > 0) {
            jedis.hmset("carts:" + userId + ":info", cartInfoMap);
        }
        jedis.close();

    }

    /**
     * 取缓存的数据
     * @param userId
     * @return
     */
    @Override
    public List<CartInfo> getCartCache(String userId) {
        Jedis jedis = redisUtil.getJedis();
        List<CartInfo> cartInfos = new ArrayList<>();

        List<String> hvals = jedis.hvals("carts:" + userId + ":info");
        if (hvals != null && hvals.size() > 0) {
            hvals.forEach(cartStr -> {
                CartInfo cartInfo = JSON.parseObject(cartStr, CartInfo.class);
                cartInfos.add(cartInfo);
            });
        }

        return cartInfos;
    }


    @Override
    public void updateCartChecked(CartInfo cartInfo) {
        Example example = new Example(CartInfo.class);
        example.createCriteria().andEqualTo("skuId", cartInfo.getSkuId()).andEqualTo("userId", cartInfo.getUserId());
        cartInfoMapper.updateByExampleSelective(cartInfo, example);

        syncCache(cartInfo.getUserId());

    }

    @Override
    public void combineCart(List<CartInfo> cartInfos, String userId) {
        if (cartInfos != null) {
            for (CartInfo cartInfo : cartInfos) {
                CartInfo info = ifCartExist(cartInfo);
                if (info == null) {
                    //插入
                    cartInfo.setUserId(userId);
                    cartInfoMapper.insertSelective(cartInfo);
                } else {
                    //更新
                    info.setSkuNum(info.getSkuNum() + cartInfo.getSkuNum());
                    info.setCartPrice(info.getCartPrice().multiply(new BigDecimal(info.getSkuNum())));
                    cartInfoMapper.updateByPrimaryKeySelective(info);
                }
            }
        }

        //同步缓存
        syncCache(userId);

    }

    @Override
    public List<CartInfo> getCartCacheByChecked(String userId) {
        Jedis jedis = redisUtil.getJedis();
        List<CartInfo> cartInfos = new ArrayList<>();

        List<String> hvals = jedis.hvals("carts:" + userId + ":info");
        if (hvals != null && hvals.size() > 0) {
            //取缓存中数据
            for (String hval : hvals) {
                CartInfo cartInfo = JSON.parseObject(hval, CartInfo.class);
                if (cartInfo.getIsChecked().equals("1")) {
                    cartInfos.add(cartInfo);
                }
            }
        } else {
            //取数据库中数据
            CartInfo cartInfo = new CartInfo();
            cartInfo.setUserId(userId);
            cartInfo.setIsChecked("1");
            cartInfos = cartInfoMapper.select(cartInfo);

        }



        return cartInfos;
    }

    @Override
    public void deleteCartById(List<CartInfo> cartInfos) {

        for (CartInfo cartInfo : cartInfos) {
            cartInfoMapper.deleteByPrimaryKey(cartInfo);
        }

        //同步缓存
        syncCache(cartInfos.get(0).getUserId());

    }

    /**
     * 删除购物车根据用户Id
     * @param userId
     */
    @Override
    public void deleteCartByUserId(String userId) {
        Example example = new Example(CartInfo.class);
        example.createCriteria().andEqualTo("userId",userId);
        this.cartInfoMapper.deleteByExample(example);
    }

}
