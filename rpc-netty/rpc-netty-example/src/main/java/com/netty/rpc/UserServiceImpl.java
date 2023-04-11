package com.netty.rpc;

import java.util.Arrays;
import java.util.List;

/**
 * @description 用户实现类
 * @date 2023/4/11 22:49
 * @author: qyl
 */
public class UserServiceImpl implements UserService{
    @Override
    public void sayHello() {
        System.out.println ("hello");
    }

    @Override
    public List<Integer> selectAll() {
        return Arrays.asList(1,2,3);
    }
}
