package io.r2mo.spring.cache.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "cache.redisson")
@Configuration
public class ConfigCacheRedission implements Serializable {
    /**
     * redis 缓存前缀 key 值
     */
    private String prefix;
    /**
     * 线程池数量，默认值 = 处理器核心数 * 2 -> setThreads
     */
    private int threads;
    /**
     * Netty 线程池数量，默认值 = 处理器核心数 * 2 -> setNettyThreads
     */
    private int threadsNetty;
    /**
     * 单机服务器配置
     */
    private ServerConfig configServer;
    /**
     * 集群服务器配置
     */
    private ClusterConfig configCluster;

    @Data
    @NoArgsConstructor
    public static class ServerConfig implements Serializable {
        /**
         * 客户端名称 -> setClientName
         */
        private String clientName;
        /**
         * 最小空闲连接数 -> setConnectionMinimumIdleSize
         */
        private int connectionIdleSize;
        /**
         * 连接池大小 -> setConnectionPoolSize
         */
        private int connectionPoolSize;
        /**
         * 连接空闲超时，单位：毫秒 -> setIdleConnectionTimeout
         */
        private int timeoutIdle;
        /**
         * 命令等待超时，单位：毫秒 -> setTimeout
         */
        private int timeout;
        /**
         * 发布和订阅连接池大小 -> setSubscriptionConnectionPoolSize
         */
        private int subscriptionPoolSize;
    }

    @Data
    @NoArgsConstructor
    public static class ClusterConfig implements Serializable {
        /**
         * master 最小空闲连接数 -> setMasterConnectionMinimumIdleSize
         */
        private int masterIdleSize;
        /**
         * master 连接池大小 -> setMasterConnectionPoolSize
         */
        private int masterPoolSize;
        /**
         * slave 最小空闲连接数 -> setSlaveConnectionMinimumIdleSize
         */
        private int slaveIdleSize;
        /**
         * slave 连接池大小 -> setSlaveConnectionPoolSize
         */
        private int slavePoolSize;
        /**
         * 客户端名称 -> setClientName
         */
        private String clientName;
        /**
         * 连接空闲超时，单位：毫秒 -> setIdleConnectionTimeout
         */
        private int timeoutIdle;
        /**
         * 命令等待超时，单位：毫秒 -> setTimeout
         */
        private int timeout;
        /**
         * 发布和订阅连接池大小 -> setSubscriptionConnectionPoolSize
         */
        private int subscriptionPoolSize;
        /**
         * 读取模式设置 -> setReadMode
         */
        private ReadMode modeRead;
        /**
         * 订阅模式设置 -> setSubscriptionMode
         */
        private SubscriptionMode modeSubscription;
    }
}
