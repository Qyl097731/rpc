package com.netty.rpc;

import com.netty.rpc.service.UserService;
import com.netty.rpc.service.impl.UserServiceImpl;

import java.io.IOException;

/**
 * @description 服务类
 * @date 2023/4/11 22:41
 * @author: qyl
 */
public class Server {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        NettyServer server = new NettyServer ();
    }
}
