package com.imooc.pay.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author gaomy
 * @Date 2021/12/30 17:35
 * @Description
 * @Version 1.0
 */

@Component
@ConfigurationProperties(prefix = "wx")
@Data
public class WxAccountConfig {
//获取配置文件的属性值
    private String appId;

    private String mchId;

    private String mchKey;

    private String notifyUrl;

    private String returnUrl;
}
