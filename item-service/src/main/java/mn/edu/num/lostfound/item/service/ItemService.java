package mn.edu.num.lostfound.item.service;

import mn.edu.num.lostfound.item.dto.CreateItemRequest;
import mn.edu.num.lostfound.item.dto.ItemResponse;
import mn.edu.num.lostfound.item.entity.Item;
import mn.edu.num.lostfound.item.entity.ItemStatus;
import mn.edu.num.lostfound.item.entity.ItemType;
import mn.edu.num.lostfound.item.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public ItemResponse createItem(CreateItemRequest request) {
        Item item = new Item();

        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory());
        item.setLocation(request.getLocation());
        item.setImageUrl(request.getImageUrl());
        item.setType(request.getType());
        item.setStatus(ItemStatus.OPEN);
        item.setContactName(request.getContactName());
        item.setContactEmail(request.getContactEmail());

        Item savedItem = itemRepository.save(item);

        return toResponse(savedItem);
    }

    public List<ItemResponse> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ItemResponse getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + id));

        return toResponse(item);
    }

    public List<ItemResponse> getItemsByType(ItemType type) {
        return itemRepository.findByType(type)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    public ItemResponse updateItemStatus(Long id, ItemStatus status) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + id));

        item.setStatus(status);

        Item savedItem = itemRepository.save(item);

        return toResponse(savedItem);
    }

    private ItemResponse toResponse(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getCategory(),
                item.getLocation(),
                item.getImageUrl(),
                item.getType(),
                item.getStatus(),
                item.getContactName(),
                item.getContactEmail(),
                item.getCreatedAt()
        );
    }
}
