package com.starry.netty.server.service;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/19 21:52
 * @Description
 */
public class HelloServiceImpl implements HelloService{

    @Override
    public String hello(String name) {
        return name;
    }
}
