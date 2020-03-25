package org.fjun.service.impl;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.fjun.client.HBaseClient;
import org.fjun.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private HBaseClient hBaseClient;

    @Override
    public boolean login(String name, String password) {
        try {
            String value = hBaseClient.getPassword(name);
            return StringUtils.equals(value, password);
        } catch (IOException e) {
            logger.error("登录异常", e);
            return false;
        }
    }

    @Override
    public boolean validate(String name) {
        return StringUtils.isNotBlank(name) && name.length() >= 8 && name.length() <= 16;
    }
}
