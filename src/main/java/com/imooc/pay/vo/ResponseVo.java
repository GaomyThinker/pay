package com.imooc.pay.vo;


import lombok.Data;

/**
 * @Author gaomy
 * @Date 2022/1/4 15:44
 * @Description
 * @Version 1.0
 */

@Data
public class ResponseVo<T> {

    private Integer status;

    private String msg;

    private T data;

    public ResponseVo(Integer status,String msg){
        this.status=status;
        this.msg=msg;
    }

    public static <T>ResponseVo<T> success(String msg){
        return new ResponseVo<>(0,msg);
    }
}
