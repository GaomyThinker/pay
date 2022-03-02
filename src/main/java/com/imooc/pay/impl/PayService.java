package com.imooc.pay.impl;


import com.google.gson.Gson;
import com.imooc.pay.dao.PayInfoMapper;
import com.imooc.pay.enums.PayPlatFormEnum;
import com.imooc.pay.pojo.PayInfo;
import com.imooc.pay.service.IPayService;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.wxpay.request.WxPayUnifiedorderRequest;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Author gaomy
 * @Date 2021/12/24 9:53
 * @Description
 * @Version 1.0
 */

@Service
@Slf4j
public class PayService implements IPayService {

    private final static String QUEUE_PAY_NOTIFY="payNotify";

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private BestPayService bestPayService;

    @Autowired
    private PayInfoMapper payInfoMapper;

    /**
     * 创建/发起支付
     * @param orderId
     * @param amount
     */
    @Override
    public PayResponse create(String orderId, BigDecimal amount,BestPayTypeEnum bestPayTypeEnum) {
        //写入数据库
        PayInfo payInfo=new PayInfo(Long.parseLong(orderId), PayPlatFormEnum.getByBestPayTypeEnum(bestPayTypeEnum).getCode(),
                OrderStatusEnum.NOTPAY.name(),amount);
        payInfoMapper.insertSelective(payInfo);

        // 第一步：设置配置 配置bestPayService

        // 第二步：发起支付
        PayRequest request=new PayRequest();
        request.setOrderName("4559066-最好的支付sdk");
        request.setOrderId(orderId);
        request.setOrderAmount(amount.doubleValue());
        request.setPayTypeEnum(bestPayTypeEnum);
        System.out.println(bestPayService);
        PayResponse response = bestPayService.pay(request);
        System.out.println(response);
//        PayResponse response=new PayResponse();
        return response;
//        log.info("reponse={}"+pay);
    }

    @Override
    public String asyncNotify(String notifyData) {
//         1. 签名校验
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("payReponse={}"+payResponse);
        // 2. 金额校验(从数据库查订单)
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(payResponse.getOrderId()));
        if (payInfo==null){
            // 比较严重（正常情况下是不会发生的） 在抛出异常之前发出告警：钉钉、短信等
            throw new RuntimeException("通过OrderNo查询到的结果为null");
        }
        // 如果订单支付状态不是成功，那么再比较金额
        if (!payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.name())){
            if (payInfo.getPayAmount().compareTo(BigDecimal.valueOf(payResponse.getOrderAmount()))!=0){
                // 告警
                throw new RuntimeException("异步通知中的金额和数据库中的不一致 orderNo="+payResponse.getOrderId());
            }
            // 3. 修改订单的支付状态
            payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
            // 交易流水号也需要填进去
            payInfo.setPlatformNumber(payResponse.getOutTradeNo());
            // 需要把修改时间设为null或当前时间,否则会直接把之前表里的时间直接填进去，不会发生修改   第二种方法就是把对应的方法中关于updateTime的插入语句删掉，完全交由数据库管理
            payInfo.setUpdateTime(null);
            payInfoMapper.updateByPrimaryKeySelective(payInfo);
        }

        //TODO pay系统发送MQ消息，mall系统接受MQ消息
        amqpTemplate.convertAndSend(QUEUE_PAY_NOTIFY, new Gson().toJson(payInfo));
         // 4.告诉微信不要再通知了，已经收到通知了 (返回给微信的数据内容，参照微信官方文档的要求)

        if (payResponse.getPayPlatformEnum()== BestPayPlatformEnum.WX){
            return  "<xml>\n"+
                    "<return_code><![CDATA[SUCCESS]]></return_code>\n"+
                    "<return_msg><![CDATA[OK]]></return_msg>\n"+
                    "</xml>";
        }else if (payResponse.getPayPlatformEnum()==BestPayPlatformEnum.ALIPAY){
            return "success";
        }

        throw new RuntimeException("异步通知中错误的支付平台");

    }

    @Override
    public PayInfo queryByOrderId(String orderId) {
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(orderId));
        return payInfo;
    }
}
