package com.netty.rpc.service;

import com.netty.rpc.domain.User;

import java.util.List;

/**
 * @description 用户接口
 * @date 2023/4/11 22:48
 * @author: qyl
 */
public interface UserService {
    /**
     * hello
     */
    void sayHello();

    /**
     * 查询所有用户id
     * @return 用户id
     */
    List<Integer> selectIds();

    /**
     * 查询所有用户信息
     * @return
     */
    List<User> selectUsers();
}
