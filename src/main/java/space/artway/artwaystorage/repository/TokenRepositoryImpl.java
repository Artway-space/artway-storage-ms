package space.artway.artwaystorage.repository;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import space.artway.artwaystorage.model.StorageType;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository<StorageType, Object> {
    @Qualifier("tokensRedisTemplate")
    private final RedisTemplate<StorageType, Object> redisTemplate;

    @Override
    public void save(StorageType key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Object findByKey(StorageType key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Long deleteByKey(StorageType key){
        return redisTemplate.delete(ImmutableList.of(key));
    }
}
