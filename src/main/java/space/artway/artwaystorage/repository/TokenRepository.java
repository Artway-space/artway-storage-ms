package space.artway.artwaystorage.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository<K, V> {
    void save(K key, V value);

    V findByKey(K key);
}
