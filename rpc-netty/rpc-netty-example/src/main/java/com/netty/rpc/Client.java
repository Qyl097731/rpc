package com.netty.rpc;

import com.netty.rpc.service.UserService;

/**
 * @description 客户端
 * @date 2023/4/11 22:50
 * @author: qyl
 */
public class Client {
    public static void main(String[] args) {
        NettyClient client = new NettyClient();
        UserService service = client.getProxy(UserService.class, "");
        service.sayHello();
        System.out.println(service.selectIds());
    }
}
