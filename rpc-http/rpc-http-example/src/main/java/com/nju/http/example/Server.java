package com.nju.http.example;

import com.nju.rpc.RpcServer;

/**
 * @description
 * @date 2023/4/9 21:39
 * @author: qyl
 */
public class Server {
    public static void main(String[] args) throws Exception {
        RpcServer server = new RpcServer ();
        server.register (UserService.class,new UserServiceImpl ());
        server.start ();
    }
}
