package org.fjun.init;

import org.fjun.config.MiddlewareFakeBeanBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Context加载完成后事件
 *
 */
public class ContextLoadedListener extends AbstractRunListener{
    public ContextLoadedListener(SpringApplication app, String... args) {
        super(app, args);
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        super.contextLoaded(context);
        new MiddlewareFakeBeanBuilder(context).build();
    }
}
