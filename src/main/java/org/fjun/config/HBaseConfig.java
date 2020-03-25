package org.fjun.config;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionConfiguration;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class HBaseConfig {
    @Autowired
    private HbaseProperties hbaseProperties;

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
        conf.set("hbase.zookeeper.quorum", hbaseProperties.getZookeeperQuorum());
        conf.set("hbase.zookeeper.property.clientPort", String.valueOf(hbaseProperties.getZookeeperClientPort()));
        conf.set(ConnectionConfiguration.WRITE_BUFFER_SIZE_KEY, String.valueOf(5 * 1024 * 1024));
        return ConnectionFactory.createConnection(conf, threadPool.getThreadPoolExecutor());
    }
}
