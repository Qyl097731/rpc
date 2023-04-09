package com.nju.http.example;

import com.nju.rpc.client.RpcClient;

/**
 * @date 2023/4/9 21:31
 * @author: qyl
 */
public class Client {
    public static void main(String[] args) {
        RpcClient client = new RpcClient ();
        UserService proxy = client.getProxy (UserService.class);
        System.out.println (proxy.selectList ());
    }
}
