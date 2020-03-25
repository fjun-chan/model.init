package org.fjun.config;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionConfiguration;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
@ConditionalOnProperty(value = "spring.hbase.enabled", havingValue = "true", matchIfMissing = true)
public class HBaseConfig {
    @Autowired
    private HBaseProperties hBaseProperties;

    @Bean
    public Connection hbaseConnection() throws IOException {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        threadPool.setThreadNamePrefix("HBASE_WRITE_THREAD_POOL");
        threadPool.setCorePoolSize(30);
        threadPool.setMaxPoolSize(50);
        threadPool.setQueueCapacity(1500);
        threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        threadPool.initialize();

        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", hBaseProperties.getZookeeperQuorum());
        conf.set("hbase.zookeeper.property.clientPort", String.valueOf(hBaseProperties.getZookeeperClientPort()));
        conf.set(ConnectionConfiguration.WRITE_BUFFER_SIZE_KEY, String.valueOf(5 * 1024 * 1024));
        return ConnectionFactory.createConnection(conf, threadPool.getThreadPoolExecutor());
    }
}
