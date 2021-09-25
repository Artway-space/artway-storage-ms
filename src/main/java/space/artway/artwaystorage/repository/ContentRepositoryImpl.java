package space.artway.artwaystorage.repository;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import space.artway.artwaystorage.model.Content;

@Repository
@RequiredArgsConstructor
public class ContentRepositoryImpl implements ContentRepository<String> {
    @Qualifier("contentRedisTemplate")
    private final RedisTemplate<String, Content> redisTemplate;

    @Override
    public void save(String id, Content value) {
        redisTemplate.opsForValue().set(id, value);
    }

    @Override
    public Content findById(String id) {
        return redisTemplate.opsForValue().get(id);
    }

    @Override
    public Long deleteById(String id) {
        return redisTemplate.delete(ImmutableList.of(id));
    }
}
