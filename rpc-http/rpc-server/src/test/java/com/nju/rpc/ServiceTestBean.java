package com.nju.rpc;

import lombok.Data;

/**
 * @description 服务测试类
 * @date 2023/4/9 17:57
 * @author: qyl
 */
@Data
public class ServiceTestBean {
    public void sayHello(){
        System.out.println ("Hello");
    }
}
