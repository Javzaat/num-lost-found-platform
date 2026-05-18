package mn.edu.num.lostfound.item.repository;

import mn.edu.num.lostfound.item.entity.Item;
import mn.edu.num.lostfound.item.entity.ItemStatus;
import mn.edu.num.lostfound.item.entity.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByType(ItemType type);

    List<Item> findByStatus(ItemStatus status);

    List<Item> findByCategoryIgnoreCase(String category);
}
