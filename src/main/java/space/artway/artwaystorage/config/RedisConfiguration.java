package space.artway.artwaystorage.config;

import io.lettuce.core.resource.ClientResources;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import space.artway.artwaystorage.model.Content;
import space.artway.artwaystorage.model.StorageType;


@Configuration
@Getter
@Setter
@EnableRedisRepositories
@ConfigurationProperties(prefix = "spring")
public class RedisConfiguration {

    private RedisProperties redis;
    private RedisProperties redis2;

    @Bean(name = "tokensRedisTemplate")
    @Primary
    public RedisTemplate<StorageType, Object> redisTemplate(@Qualifier("tokensRedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        RedisTemplate<StorageType, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean(name = "contentRedisTemplate")
    public RedisTemplate<String, Content> contentRedisTemplate(@Qualifier("contentRedisConnectionFactory") RedisConnectionFactory connectionFactory){
        RedisTemplate<String, Content> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    @Primary
    public LettuceConnectionFactory tokensRedisConnectionFactory(ClientResources clientResources){
        RedisStandaloneConfiguration redisStandaloneConfiguration = getStandaloneConfig(redis);
        LettuceClientConfiguration clientConfiguration = getLettuceClientConfiguration(clientResources, redis);

        return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfiguration);
    }

    @Bean
    public LettuceConnectionFactory contentRedisConnectionFactory(ClientResources clientResources){
        RedisStandaloneConfiguration redisStandaloneConfiguration = getStandaloneConfig(redis2);
        LettuceClientConfiguration clientConfiguration = getLettuceClientConfiguration(clientResources, redis2);

        return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfiguration);
    }


    private RedisStandaloneConfiguration getStandaloneConfig(RedisProperties redisProperties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost());
        config.setPort(redisProperties.getPort());
        config.setPassword(RedisPassword.of(redisProperties.getPassword()));
        config.setDatabase(redisProperties.getDatabase());
        return config;
    }

    private LettuceClientConfiguration getLettuceClientConfiguration(ClientResources clientResources,
                                                                     RedisProperties redisProperties) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder =
                createBuilder(redisProperties.getLettuce().getPool());
        if (redisProperties.isSsl()) {
            builder.useSsl();
        }
        if (redisProperties.getTimeout() != null) {
            builder.commandTimeout(redisProperties.getTimeout());
        }
        if (redisProperties.getLettuce() != null) {
            RedisProperties.Lettuce lettuce = redisProperties.getLettuce();
            if (lettuce.getShutdownTimeout() != null
                    && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(
                        redisProperties.getLettuce().getShutdownTimeout());
            }
        }
        builder.clientResources(clientResources);
        return builder.build();
    }

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool pool) {
        if (pool == null) {
            return LettuceClientConfiguration.builder();
        }
        return LettucePoolingClientConfiguration.builder()
                .poolConfig(getPoolConfig(pool));
    }

    private GenericObjectPoolConfig getPoolConfig(RedisProperties.Pool properties) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(properties.getMaxActive());
        config.setMaxIdle(properties.getMaxIdle());
        config.setMinIdle(properties.getMinIdle());
        if (properties.getMaxWait() != null) {
            config.setMaxWaitMillis(properties.getMaxWait().toMillis());
        }
        return config;
    }

}
