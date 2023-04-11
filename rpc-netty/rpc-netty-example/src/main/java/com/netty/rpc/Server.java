package com.netty.rpc;

/**
 * @description 服务类
 * @date 2023/4/11 22:41
 * @author: qyl
 */
public class Server {
    public static void main(String[] args) {
        NettyServer server = new NettyServer ();
        server.register(UserService.class,new UserServiceImpl ());
        server.start();
    }
}
