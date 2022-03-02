package com.imooc.pay;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PayApplicationTests {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void contextLoads() {
    }

    @Test
    public void sendMQMsg(){
        // payNotify是列名  hello是发送的消息
        amqpTemplate.convertAndSend("payNotify","hello");
    }

}
