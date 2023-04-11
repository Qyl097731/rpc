package com.netty.rpc;

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
     * 查询所有
     * @return 用户id
     */
    List<Integer> selectAll();
}
