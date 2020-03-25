package org.fjun.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("mw.hbase")
@Component
public class HbaseProperties {
    /**
     * zookeeper IP/域名
     */
    private String zookeeperQuorum;
    /**
     * zookeeper 端口
     */
    private int zookeeperClientPort;
    /**
     * 是否带snappy算法
     */
    private boolean hasSnappy = true;

    public String getZookeeperQuorum() {
        return zookeeperQuorum;
    }

    public HbaseProperties setZookeeperQuorum(String zookeeperQuorum) {
        this.zookeeperQuorum = zookeeperQuorum;
        return this;
    }

    public int getZookeeperClientPort() {
        return zookeeperClientPort;
    }

    public HbaseProperties setZookeeperClientPort(int zookeeperClientPort) {
        this.zookeeperClientPort = zookeeperClientPort;
        return this;
    }

    public boolean isHasSnappy() {
        return hasSnappy;
    }

    public HbaseProperties setHasSnappy(boolean hasSnappy) {
        this.hasSnappy = hasSnappy;
        return this;
    }
}
