package mn.edu.num.lostfound.matching.dto;

import java.time.LocalDateTime;

public record ItemDto(
        Long id,
        String title,
        String description,
        String category,
        String location,
        String type,
        String status,
        String contactName,
        String contactEmail,
        LocalDateTime createdAt
) {
}
