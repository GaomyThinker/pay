package com.imooc.pay.enums;


import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import lombok.Getter;

/**
 * @Author gaomy
 * @Date 2021/12/30 14:22
 * @Description
 * @Version 1.0
 */

@Getter
public enum PayPlatFormEnum {

    //1支付宝 2微信
    ALIPAY(1),
    WX(2),
    ;
    Integer code;
    PayPlatFormEnum(Integer code){
        this.code=code;
    }

    //把sdk中的枚举和PayPlatFormEnum合在一起
   public static PayPlatFormEnum getByBestPayTypeEnum(BestPayTypeEnum bestPayTypeEnum){
//        if (bestPayTypeEnum.getPlatform().getName().equals(PayPlatFormEnum.ALIPAY.name())){
//            return PayPlatFormEnum.ALIPAY;
//        }else if (bestPayTypeEnum.getPlatform().getName().equals(PayPlatFormEnum.WX.name())){
//            return PayPlatFormEnum.WX;
//        }
        for (PayPlatFormEnum payPlatFormEnum : PayPlatFormEnum.values()) {
            if (bestPayTypeEnum.getPlatform().name().equals(payPlatFormEnum.name())){
                return payPlatFormEnum;
            }
        }
        throw  new RuntimeException("错误的支付平台"+bestPayTypeEnum.name());
    }
}
