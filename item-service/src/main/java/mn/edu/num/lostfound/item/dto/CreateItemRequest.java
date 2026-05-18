package mn.edu.num.lostfound.item.dto;

import mn.edu.num.lostfound.item.entity.ItemType;

public class CreateItemRequest {

    private String title;
    private String description;
    private String category;
    private String location;
    private String imageUrl;
    private ItemType type;
    private String contactName;
    private String contactEmail;

    public CreateItemRequest() {
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

    public String getContactName() {
        return contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }
}
