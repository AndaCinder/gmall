package com.lichen.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.lichen.gmall.annotation.LoginRequire;
import com.lichen.gmall.bean.*;
import com.lichen.gmall.order.utils.AlipayTemplate;
import com.lichen.gmall.order.vo.PayAsyncVo;
import com.lichen.gmall.order.vo.PayVo;
import com.lichen.gmall.vo.StockDelVo;
import com.lichen.gmall.service.CartService;
import com.lichen.gmall.service.OrderService;
import com.lichen.gmall.service.SkuService;
import com.lichen.gmall.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class OrderController {

    @Reference
    CartService cartService;
    @Reference
    UserService userService;
    @Reference
    OrderService orderService;
    @Reference
    SkuService skuService;
    @Autowired
    private AlipayTemplate alipayTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @LoginRequire(ifNeedSuccess = true)
    @RequestMapping("submitOrder")
    @ResponseBody
    public String submitOrder(HttpServletRequest request, HttpServletResponse response, ModelMap map, String tradeCode) {
        String userId = (String) request.getAttribute("userId");
        //比较交易码
        boolean bTrade = orderService.checkTradeCode(tradeCode, userId);
        //订单对象
        OrderInfo orderInfo = new OrderInfo();
        List<OrderDetail> orderDetails = new ArrayList<>();

        //执行提交订单业务
        if (bTrade) {

            //获取购物车中被选中的商品数据
            List<CartInfo> cartInfos = cartService.getCartCacheByChecked(userId);

            //生成订单信息
            for (CartInfo cartInfo : cartInfos) {
                OrderDetail orderDetail = new OrderDetail();
                String skuId = cartInfo.getSkuId();
                BigDecimal skuPrice = cartInfo.getSkuPrice();
                //验价
                boolean bPrice = skuService.checkPrice(skuPrice, skuId);
                //验库存

                if (bPrice) {
                    orderDetail.setSkuName(cartInfo.getSkuName());
                    orderDetail.setSkuId(cartInfo.getSkuId());
                    orderDetail.setOrderPrice(cartInfo.getCartPrice());
                    orderDetail.setImgUrl(cartInfo.getImgUrl());
                    orderDetail.setSkuNum(cartInfo.getSkuNum());
                    orderDetails.add(orderDetail);
                } else {
                    map.put("errMsg", "订单中的商品价格（库存）发生了变化，请重新选择订单");
                    return "tradeFail";
                }

            }
            orderInfo.setOrderDetailList(orderDetails);

            //封装订单信息
            orderInfo.setProcessStatus("订单未支付");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            orderInfo.setExpireTime(calendar.getTime());
            orderInfo.setOrderStatus("未支付");
            String consignee = "测试收件人";
            orderInfo.setConsignee(consignee);
            //外部订单号
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentTime = sdf.format(new Date());
            String outTradeNo = "lichen" + currentTime + System.currentTimeMillis();
            orderInfo.setOutTradeNo(outTradeNo);
            orderInfo.setPaymentWay("支付宝");
            orderInfo.setUserId(userId);
            orderInfo.setTotalAmount(getTotalPrice(cartInfos));
            orderInfo.setOrderComment("订单");
            String address = "测试收件地址";
            orderInfo.setDeliveryAddress(address);
            orderInfo.setCreateTime(new Date());
            String tel = "123123";
            orderInfo.setConsigneeTel(tel);
            orderInfo.setWareStatus("有货");
            

            orderService.saveOrder(orderInfo);

            //删除购物车中提交的商品信息，同步缓存
            cartService.deleteCartById(cartInfos);
        } else {
            map.put("errMsg", "订单中号不匹配");
            return "tradeFail";
        }
        // 这里加入交易接口
        // 跳转支付宝
        PayVo payVo = new PayVo()
                .setOut_trade_no(orderInfo.getOutTradeNo())
                .setTotal_amount(orderInfo.getTotalAmount().toString())
                .setSubject("谷粒商城收银台")
                .setBody("商城订单支付");
        try {
            String pay = this.alipayTemplate.pay(payVo);
//            log.info("{}",pay);
//            System.out.println(pay);
            return pay;
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return "支付订单出现错误，请重试";
    }

    @LoginRequire(ifNeedSuccess = true)
    @RequestMapping("toTrade")
    public String toTrade(HttpServletRequest request, HttpServletResponse response, ModelMap map) {
        String userId = (String) request.getAttribute("userId");

        //将选中的购物车对象转化为订单对象
        List<CartInfo> cartInfos = cartService.getCartCacheByChecked(userId);
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartInfo cartInfo : cartInfos) {
            OrderDetail orderDetail = new OrderDetail();
            //将购物车对象转化为订单对象
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(cartInfo.getCartPrice());
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());

            orderDetails.add(orderDetail);
        }

        //查询用户收货地址，让用户选择
        List<UserAddress> userAddresses = userService.getUserAddressList(userId);

        //生成交易码
        String traderCode = orderService.genTradeCode(userId);

        map.put("userAddressList", userAddresses);
        map.put("orderDetailList", orderDetails);
        map.put("totalAmount", getTotalPrice(cartInfos));
        map.put("tradeCode",traderCode);

        //发送消息去删除购物车信息
        this.amqpTemplate.convertAndSend("order-exchange","cart.delete",userId);

        // 减库存
        if (!CollectionUtils.isEmpty(orderDetails)){
            this.amqpTemplate.convertAndSend("order-exchange","stock.delete",JSON.toJSONString(
                    orderDetails.parallelStream().map(orderDetail -> new StockDelVo().setSkuId(orderDetail.getSkuId()).setSkuNum(orderDetail.getSkuNum())).collect(Collectors.toList()))
            );
        }
        return "trade";
    }

    /**
     * 支付宝成功回调
     * @param asyncVo
     * @return
     */
    @PostMapping("pay/success")
    @ResponseBody
    public Object paySuccess(PayAsyncVo asyncVo){
        log.info("支付成功,订单号：{}",asyncVo.getOut_trade_no());
        // 支付成功减库存
        this.amqpTemplate.convertAndSend("order-exchange","order.success",asyncVo.getOut_trade_no());
        return "success";
    }

    private BigDecimal getTotalPrice(List<CartInfo> cartInfos) {
        BigDecimal totalPrice = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfos) {
            totalPrice = totalPrice.add(cartInfo.getCartPrice());
        }
        return totalPrice;
    }

    @GetMapping("pay/payTest")
    public String payTest(){
        return "payTest";
    }

}
