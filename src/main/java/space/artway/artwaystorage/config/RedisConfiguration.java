package space.artway.artwaystorage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import space.artway.artwaystorage.model.StorageType;


@Configuration
@EnableRedisRepositories
public class RedisConfiguration {

    @Bean
    public RedisTemplate<StorageType, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<StorageType, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

}
