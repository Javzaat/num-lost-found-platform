package mn.edu.num.lostfound.item.controller;

import mn.edu.num.lostfound.item.dto.CreateItemRequest;
import mn.edu.num.lostfound.item.dto.ItemResponse;
import mn.edu.num.lostfound.item.entity.ItemType;
import mn.edu.num.lostfound.item.entity.ItemStatus;
import mn.edu.num.lostfound.item.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(@RequestBody CreateItemRequest request) {
        return itemService.createItem(request);
    }

    @GetMapping
    public List<ItemResponse> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/lost")
    public List<ItemResponse> getLostItems() {
        return itemService.getItemsByType(ItemType.LOST);
    }

    @GetMapping("/found")
    public List<ItemResponse> getFoundItems() {
        return itemService.getItemsByType(ItemType.FOUND);
    }


    @PatchMapping("/{id}/status/{status}")
    public ItemResponse updateItemStatus(
            @PathVariable Long id,
            @PathVariable ItemStatus status
    ) {
        return itemService.updateItemStatus(id, status);
    }

    @GetMapping("/type/{type}")
    public List<ItemResponse> getItemsByType(@PathVariable ItemType type) {
        return itemService.getItemsByType(type);
    }

    @GetMapping("/{id}")
    public ItemResponse getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }
}