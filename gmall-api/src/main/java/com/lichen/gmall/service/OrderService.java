package com.lichen.gmall.service;

import com.lichen.gmall.bean.OrderInfo;

public interface OrderService {
    String genTradeCode(String userId);

    boolean checkTradeCode(String tradeCode, String userId);

    void saveOrder(OrderInfo orderInfo);

    void updateOrder(String out_trade_no);
}
