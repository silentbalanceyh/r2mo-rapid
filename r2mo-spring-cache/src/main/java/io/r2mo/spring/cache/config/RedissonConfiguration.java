package io.r2mo.spring.cache.config;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thread.Threading;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.VirtualThreadTaskExecutor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.TimeZone;

/**
 * 此处主要是解决循环引用的问题，和 {@link RedisConfiguration} 中的初始化分离开
 *
 * @author lang : 2025-12-02
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ConfigCache.class)
public class RedissonConfiguration {

    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    @Autowired
    private ConfigCache configuration;

    private static boolean isVirtual() {
        return Threading.VIRTUAL.isActive(SpringUtil.getBean(Environment.class));
    }

    @Bean
    public RedissonAutoConfigurationCustomizer redissonCustomizer() {
        final ConfigCacheRedission redisson = this.configuration.getRedisson();
        if (Objects.isNull(redisson)) {
            log.warn("[ R2MO ] Redisson 配置未启用，跳过 RedissonAutoConfigurationCustomizer 初始化，请检查您的配置！");
            return null;
        }
        return config -> {
            // 组合序列化 key 使用 String 内容处理通用 json 格式
            final CompositeCodec codec = this.redissonCompositeCodec();
            config.setThreads(redisson.getThreads())
                .setNettyThreads(redisson.getThreadsNetty())
                // 缓存 Lua 脚本，减少网络传输
                .setUseScriptCache(true)
                .setCodec(codec);
            if (isVirtual()) {
                config.setNettyExecutor(new VirtualThreadTaskExecutor("r2mo-redisson-"));
            }


            final ConfigCacheRedission.ServerConfig serverConfig = redisson.getConfigServer();
            if (Objects.nonNull(serverConfig)) {
                // 使用单机模式
                config.useSingleServer()
                    .setNameMapper(new RedisNameMapper(redisson.getPrefix()))
                    .setTimeout(serverConfig.getTimeout())
                    .setClientName(serverConfig.getClientName())
                    .setIdleConnectionTimeout(serverConfig.getTimeoutIdle())
                    .setSubscriptionConnectionPoolSize(serverConfig.getSubscriptionPoolSize())
                    .setConnectionMinimumIdleSize(serverConfig.getConnectionIdleSize())
                    .setConnectionPoolSize(serverConfig.getConnectionPoolSize());
            }


            final ConfigCacheRedission.ClusterConfig clusterConfig = redisson.getConfigCluster();
            if (Objects.nonNull(clusterConfig)) {
                // 使用集群模式
                config.useClusterServers()
                    .setNameMapper(new RedisNameMapper(redisson.getPrefix()))
                    .setTimeout(clusterConfig.getTimeout())
                    .setClientName(clusterConfig.getClientName())
                    .setIdleConnectionTimeout(clusterConfig.getTimeoutIdle())
                    .setSubscriptionConnectionPoolSize(clusterConfig.getSubscriptionPoolSize())
                    .setMasterConnectionMinimumIdleSize(clusterConfig.getMasterIdleSize())
                    .setMasterConnectionPoolSize(clusterConfig.getMasterPoolSize())
                    .setSlaveConnectionMinimumIdleSize(clusterConfig.getSlaveIdleSize())
                    .setSlaveConnectionPoolSize(clusterConfig.getSlavePoolSize())
                    .setReadMode(clusterConfig.getModeRead())
                    .setSubscriptionMode(clusterConfig.getModeSubscription());
            }
            log.info("[ R2MO ] Redisson 客户端初始化完成 / 模式 ： {} ", Objects.nonNull(serverConfig) ? "单机模式" : "集群模式");
        };
    }

    /**
     * 临时处理
     *
     * @return CompositeCodec
     */
    private CompositeCodec redissonCompositeCodec() {
        final JavaTimeModule moduleJavaTime = new JavaTimeModule();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
        moduleJavaTime.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        moduleJavaTime.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));

        final ObjectMapper om = new ObjectMapper();
        om.registerModules(moduleJavaTime);
        om.setTimeZone(TimeZone.getDefault());
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        final TypedJsonJacksonCodec jsonCodec = new TypedJsonJacksonCodec(Object.class, om);
        return new CompositeCodec(StringCodec.INSTANCE, jsonCodec, jsonCodec);
    }

}
