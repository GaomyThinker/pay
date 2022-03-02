package com.imooc.pay.impl;

import com.imooc.pay.PayApplicationTests;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


/**
 * @Author gaomy
 * @Date 2021/12/24 10:28
 * @Description
 * @Version 1.0
 */

@Transactional
public class PayServiceTest extends PayApplicationTests {
    @Autowired
    private PayService payService;

    @Test
    public void create() {
        //BigDecimal.valueOf(0.01) 等同于 new BigDecimal("0.01")
        payService.create("1234560", BigDecimal.valueOf(0.01), BestPayTypeEnum.WXPAY_NATIVE);

    }
}