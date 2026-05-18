package mn.edu.num.lostfound.item.dto;

import mn.edu.num.lostfound.item.entity.ItemStatus;
import mn.edu.num.lostfound.item.entity.ItemType;

import java.time.LocalDateTime;

public class ItemResponse {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String location;
    private String imageUrl;
    private ItemType type;
    private ItemStatus status;
    private String contactName;
    private String contactEmail;
    private LocalDateTime createdAt;

    public ItemResponse() {
    }

    public ItemResponse(
            Long id,
            String title,
            String description,
            String category,
            String location,
            String imageUrl,
            ItemType type,
            ItemStatus status,
            String contactName,
            String contactEmail,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.imageUrl = imageUrl;
        this.type = type;
        this.status = status;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ItemType getType() {
        return type;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
