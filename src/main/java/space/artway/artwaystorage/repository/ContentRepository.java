package space.artway.artwaystorage.repository;

import org.springframework.stereotype.Repository;
import space.artway.artwaystorage.model.Content;

@Repository
public interface ContentRepository<ID> {
    void save(ID id, Content value);

    Content findById(ID id);

    Long deleteById(ID key);
}
