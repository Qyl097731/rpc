package com.netty.rpc.service.impl;

import com.netty.rpc.service.UserService;
import com.netty.rpc.domain.User;
import com.rpc.netty.annotation.RpcService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * @description 用户实现类
 * @date 2023/4/11 22:49
 * @author: qyl
 */
@RpcService
public class UserServiceImpl implements UserService {
    private List<User> users = IntStream.range(0, 10).mapToObj((id) ->
            new User(id, "abc", "123456789", 31, "江苏省南京市鼓楼区汉口路22号", "123455678@qq.com", "110")
    ).collect(Collectors.toList());

    @Override
    public void sayHello() {
        System.out.println("hello");
    }

    @Override
    public List<Integer> selectIds() {
        return Arrays.asList(1, 2, 3);
    }

    @Override
    public List<User> selectUsers() {
        return users;
    }
}
