package com.netty.rpc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户
 * @author nsec
 */
@Data
@AllArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String password;
    private Integer age;
    private String address;
    private String email;
    private String phone;
}
