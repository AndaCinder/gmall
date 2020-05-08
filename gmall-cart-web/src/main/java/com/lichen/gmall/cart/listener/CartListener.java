package com.lichen.gmall.cart.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lichen.gmall.service.CartService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * 删除购物车
 * @author 李琛
 * 2020/4/30 - 22:48
 */
@Slf4j
@Component
public class CartListener {

    @Reference
    private CartService cartService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "cart-delete-queue",durable = "true"),
            exchange = @Exchange(value = "order-exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = "cart.delete"
    ))
    public void listener(String userId, Channel channel, Message message) throws IOException {
        try {
            log.info("删除购物车数据用户Id:{}",userId);
            // 删除购物车 DB 和 redis
            this.redisTemplate.delete("carts:" + userId + ":info");
            //DB
            this.cartService.deleteCartByUserId(userId);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            if (message.getMessageProperties().getRedelivered()){
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
            }
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        }
    }
}
