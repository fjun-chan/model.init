package org.fjun.service;

public interface UserService {

    /**
     * 登录
     */
    boolean login(String name, String password);

    /**
     *  名称校验
     */
    boolean validate(String name);
}
