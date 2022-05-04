package com.starry.netty.server.service;

/**
 * @author starry
 */
public abstract class UserServiceFactory {

    private static UserService userService = new UserServiceMemoryImpl();

    public static UserService getUserService() {
        return userService;
    }
}
