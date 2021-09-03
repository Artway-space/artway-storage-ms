package space.artway.artwaystorage.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import space.artway.artwaystorage.model.StorageType;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository<StorageType, Object> {
    private final RedisTemplate<StorageType, Object> redisTemplate;

    @Override
    public void save(StorageType key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Object findByKey(StorageType key) {
        return redisTemplate.opsForValue().get(key);
    }
}
