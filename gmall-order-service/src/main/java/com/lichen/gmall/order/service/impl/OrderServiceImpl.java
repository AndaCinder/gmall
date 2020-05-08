package com.lichen.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.lichen.gmall.bean.OrderDetail;
import com.lichen.gmall.bean.OrderInfo;
import com.lichen.gmall.order.mapper.OrderDetailMapper;
import com.lichen.gmall.order.mapper.OrderInfoMapper;
import com.lichen.gmall.service.OrderService;
import com.lichen.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    OrderInfoMapper orderInfoMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Override
    public String genTradeCode(String userId) {

        String key = "user:" + userId + ":tradeCode";
        String val = UUID.randomUUID().toString();

        Jedis jedis = redisUtil.getJedis();
        jedis.setex(key, 60 * 30, val);
        jedis.close();

        return val;
    }

    @Override
    public boolean checkTradeCode(String tradeCode, String userId) {
        boolean bool = false;

        String key = "user:" + userId + ":tradeCode";
        Jedis jedis = redisUtil.getJedis();
        String val = jedis.get(key);

        if (tradeCode.equals(val)) {
            bool = true;
            jedis.del(key);
        }

        return bool;
    }

    @Override
    public void saveOrder(OrderInfo orderInfo) {

        orderInfoMapper.insertSelective(orderInfo);
        String orderId = orderInfo.getId();

        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderId);
            orderDetailMapper.insertSelective(orderDetail);
        }

    }

    /**
     * 订单支付成功修改订单状态
     */
    @Override
    public void updateOrder(String out_trade_no) {
        Example example = new Example(OrderInfo.class);
        example.createCriteria().andEqualTo("outTradeNo",out_trade_no);
        this.orderInfoMapper.updateByExampleSelective(new OrderInfo().setOrderStatus("已支付").setProcessStatus("订单已支付"),example);
    }

}
