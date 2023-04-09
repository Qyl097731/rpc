package com.nju.http.example;

import java.util.Arrays;
import java.util.List;

/**
 * @description
 * @date 2023/4/9 21:32
 * @author: qyl
 */
public class UserServiceImpl implements UserService {
    @Override
    public List<Integer> selectList() {
        return Arrays.asList (1,2,3);
    }
}
