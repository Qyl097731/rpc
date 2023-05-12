package com.netty.rpc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户
 * @author nsec
 */
@Data
@AllArgsConstructor
public class User implements Serializable {
    private Integer id;
    private String username;
    private String password;
    private Integer age;
    private String address;
    private String email;
    private String phone;
}
