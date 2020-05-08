package com.lichen.gmall.order.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.lichen.gmall.vo.StockDelVo;
import com.lichen.gmall.service.OrderService;
import com.lichen.gmall.service.WareSkuService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author 李琛
 * 2020/4/30 - 22:12
 */
@Slf4j
@Component
public class OrderListener {

    @Reference
    private OrderService orderService;

    @Reference
    private WareSkuService wareSkuService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order-pay-queue",durable = "true"),
            exchange = @Exchange(
                    value = "order-exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = "order.success"
    ))
    public void listener(String out_trade_no, Channel channel, Message message) throws IOException {
        try {
            log.info("删除库存订单信息:{}",out_trade_no);
            // 修改订单状态
            this.orderService.updateOrder(out_trade_no);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            if (message.getMessageProperties().getRedelivered()){
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
            }
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "stock-delete-queue",durable = "true"),
            exchange = @Exchange(
                    value = "order-exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = "stock.delete"
    ))
    public void stockDelete(String jsonArray, Channel channel, Message message) throws IOException {
        try {
            log.info("减库存:{}", jsonArray);
            List<StockDelVo> stockDelVos = JSON.parseArray(jsonArray, StockDelVo.class);
            this.wareSkuService.deleteByUserIds(stockDelVos);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            if (message.getMessageProperties().getRedelivered()){
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
            }
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }
    }

}
