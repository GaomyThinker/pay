package com.imooc.pay.controller;


import com.imooc.pay.impl.PayService;
import com.imooc.pay.pojo.PayInfo;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author gaomy
 * @Date 2021/12/27 10:09
 * @Description
 * @Version 1.0
 */

//@Controller
@RestController
@RequestMapping("/pay")
@Slf4j
public class PayController {

    @Autowired
    private PayService payService;

    @Autowired
    private WxPayConfig wxPayConfig;

    /**
     * 要用浏览器访问，因为如果直接发起get请求那么返回的是html源码
     * @param orderId
     * @param amount
     * @RequestParam("orderId") String orderId,
     *                                @RequestParam("amount") BigDecimal amount
     * @return
     */
    @GetMapping("/create")
    public ModelAndView create(@RequestParam("orderId") String orderId,
                                  @RequestParam("amount") BigDecimal amount,
                               @RequestParam("payType")BestPayTypeEnum bestPayTypeEnum){
        PayResponse payResponse=payService.create(orderId, amount,bestPayTypeEnum);

        Map<String,String> map=new HashMap();
//        map.put("codeUrl",payResponse.getCodeUrl());
////            return new ModelAndView("createForWxNative",map);
//        //        map.put("codeUrl","http://qr61.cn/oivUPN/qENHjbF");
//        map.put("codeUrl","wxp://f2f0csias51GBMc7UKKQ2vYyjCJXzejfkhD5X6TesufFneEK6kfQtdrKlTIQ0CyjF7pZ");
//        map.put("orderId",orderId);
//        map.put("returnUrl",wxPayConfig.getReturnUrl());
//        return new ModelAndView("createForWxNative",map);

//        支付方式不同，渲染就不同，WXPAY_NATIVE使用codeUrl，ALIPAY_PC使用body
        if (bestPayTypeEnum==BestPayTypeEnum.WXPAY_NATIVE){
            map.put("codeUrl",payResponse.getCodeUrl());
            map.put("orderId",orderId);
            map.put("returnUrl",wxPayConfig.getReturnUrl());
//            return new ModelAndView("createForWxNative",map);
            //        map.put("codeUrl","http://qr61.cn/oivUPN/qENHjbF");
                    map.put("codeUrl","wxp://f2f0csias51GBMc7UKKQ2vYyjCJXzejfkhD5X6TesufFneEK6kfQtdrKlTIQ0CyjF7pZ");
        }else if (bestPayTypeEnum==BestPayTypeEnum.ALIPAY_PC){
//            map.put("body",payResponse.getBody());
            map.put("body","<form id='bestPayForm' name=\"punchout_form\" method=\"post\" action=\"https://openapi.alipay.com//gateway.do?charset=utf-8&method=alipay.trade.page.pay&sign=XFEYlTDs8xLpOZTIIZB8QS6Q7CcAD62guP15DRHi2Xsy1pYx0XQNcp6CDoOjg7C6DG8Utz16d%2FFGPdbtObZnoG11MR8BYQ%2FJvCf2heh0t5mGktQasM8M2vna%2BCWuKHcDrM%2FhwC6p%2BZXQdvcjchE%2FIJatC93GCF5eBBLUcv7vBi37KTs7p3nFKU2NZiKyQEwz6V0jJGdjWCo8BTvbTGzTIY0KVeS4jCMerrxCTaMcCVrVgMZ%2FDiDDtEBOCJvdu3M%2FuGFhzCmBvljleODy7egZ%2BfZJUT7DNLC%2BxoIaBgXIBN85ruiZihXRzY7sckmB2zpOdD7sl3A9HQjwcCR%2FYOpQSg%3D%3D&return_url=http%3A%2F%2F127.0.0.1&notify_url=http%3A%2F%2F5i2esc.natappfree.cc%2Fpay%2Fnotify&app_id=2018062960540016&sign_type=RSA2&version=1.0&timestamp=2021-12-29+16%3A33%3A36\">\n" +
                    "<input type=\"hidden\" name=\"biz_content\" value=\"{&quot;out_trade_no&quot;:&quot;12345&quot;,&quot;total_amount&quot;:&quot;0.02&quot;,&quot;subject&quot;:&quot;4559066-最好的支付sdk&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;}\">\n" +
                    "<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n" +
                    "</form>\n" +
                    "<script>document.getElementById('bestPayForm').submit();</script>");
            return new ModelAndView("createForAlipayPC",map);
        }
        throw new RuntimeException("暂不支持的支付类型");

    }

    // 微信异步通知请求的方法
    @PostMapping("/notify")
    @ResponseBody
    public String asyncNotify(@RequestBody String notifyData){
        String s = payService.asyncNotify(notifyData);
        return s;
    }

    @GetMapping("/queryByOrderId")
    @ResponseBody
    public PayInfo queryByOrderId(@RequestParam String orderId){
        return payService.queryByOrderId(orderId);
    }
}
