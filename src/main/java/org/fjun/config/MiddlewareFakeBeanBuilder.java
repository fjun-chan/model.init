package org.fjun.config;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

public class MiddlewareFakeBeanBuilder {
    private final ConfigurableApplicationContext context;

    public MiddlewareFakeBeanBuilder(ConfigurableApplicationContext context) {
        this.context = context;
    }

    public void build() {
        for (MiddlewareAutoConfigInfo info : MiddlewareAutoConfigInfo.values()) {
            if (info == null) {
                continue;
            }
            registerFakeBean(info);
        }
    }

    private void registerFakeBean(MiddlewareAutoConfigInfo info) {
        Environment env = context.getEnvironment();
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();

        String value = env.getProperty(info.config);
        if (StringUtils.isBlank(value) || StringUtils.equalsIgnoreCase("true", value)) {
            return;
        }
        Method[] methods = info.clazz.getMethods();
        if (methods.length == 0) {
            return;
        }
        for (Method method : methods) {
            Bean bean = method.getAnnotation(Bean.class);
            if (bean == null) {
                continue;
            }
            Class<?> clazz = method.getReturnType();
            String[] names = bean.name();
            Object obj = new FakeBean().bind(info, clazz);

            if (names.length <= 0) {
                beanFactory.registerSingleton(clazz.getName(), obj);
            } else {
                for (String name : names) {
                    beanFactory.registerSingleton(name, obj);
                }
            }
        }
    }


    public static class FakeBean implements MethodInterceptor {
        private MiddlewareAutoConfigInfo info;
        public Object bind(MiddlewareAutoConfigInfo info, Class<?> clazz) {
            this.info = info;
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(clazz);
            enhancer.setCallback(this);
            return enhancer.create();
        }

        @Override
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            String methodName = method.getName();
            if (StringUtils.equals("equals", methodName)) {
                if (args == null || args.length <= 0) {
                    return false;
                }
                Object obj = args[0];
                if (obj == null) {
                    return false;
                }
                return this == obj;
            }
            if (StringUtils.equals("onApplicationEvent", methodName)) {
                return null;
            }
            throw new UnsupportedOperationException(info.name + " 中间件未加载，使用配置：" + info.config  + "=true 打开。");
        }
    }

    public enum MiddlewareAutoConfigInfo {
        /**
         * hbase
         */
        HBASE( "hbase","spring.hbase.enabled", HBaseConfig.class),
        ;

        private final String name;
        private final String config;
        private final Class<?> clazz;

        MiddlewareAutoConfigInfo(String name, String config, Class<?> clazz) {
            this.name = name;
            this.config = config;
            this.clazz = clazz;
        }
    }
}
