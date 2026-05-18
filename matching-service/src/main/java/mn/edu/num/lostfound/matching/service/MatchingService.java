package mn.edu.num.lostfound.matching.service;

import mn.edu.num.lostfound.matching.dto.ItemDto;
import mn.edu.num.lostfound.matching.dto.MatchResultDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MatchingService {

    private final RestClient restClient;
    private final String itemServiceUrl;

    public MatchingService(@Value("${item.service.url}") String itemServiceUrl) {
        this.itemServiceUrl = itemServiceUrl;
        this.restClient = RestClient.create();
    }

    public List<MatchResultDto> findMatches() {
        List<ItemDto> lostItems = fetchItemsByType("LOST");
        List<ItemDto> foundItems = fetchItemsByType("FOUND");

        List<MatchResultDto> results = new ArrayList<>();

        for (ItemDto lost : lostItems) {
            for (ItemDto found : foundItems) {
                int score = calculateScore(lost, found);

                if (score >= 40) {
                    results.add(new MatchResultDto(
                            lost.id(),
                            found.id(),
                            lost.title(),
                            found.title(),
                            score,
                            buildReason(lost, found, score)
                    ));
                }
            }
        }

        results.sort(Comparator.comparingInt(MatchResultDto::score).reversed());
        return results;
    }

    public List<MatchResultDto> findMatchesForLostItem(Long lostItemId) {
        List<ItemDto> allLostItems = fetchItemsByType("LOST");
        List<ItemDto> foundItems = fetchItemsByType("FOUND");

        ItemDto targetLostItem = allLostItems.stream()
                .filter(item -> item.id().equals(lostItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Lost item not found: " + lostItemId));

        List<MatchResultDto> results = new ArrayList<>();

        for (ItemDto found : foundItems) {
            int score = calculateScore(targetLostItem, found);

            if (score >= 40) {
                results.add(new MatchResultDto(
                        targetLostItem.id(),
                        found.id(),
                        targetLostItem.title(),
                        found.title(),
                        score,
                        buildReason(targetLostItem, found, score)
                ));
            }
        }

        results.sort(Comparator.comparingInt(MatchResultDto::score).reversed());
        return results;
    }

    private List<ItemDto> fetchItemsByType(String type) {
        return restClient.get()
                .uri(itemServiceUrl + "/api/items/type/" + type)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ItemDto>>() {});
    }

    private int calculateScore(ItemDto lost, ItemDto found) {
        int score = 0;

        if (equalsIgnoreCase(lost.category(), found.category())) {
            score += 35;
        }

        if (equalsIgnoreCase(lost.location(), found.location())) {
            score += 25;
        }

        if (containsSimilarText(lost.title(), found.title())) {
            score += 20;
        }

        if (containsSimilarText(lost.description(), found.description())) {
            score += 15;
        }

        if (score > 100) {
            return 100;
        }

        return score;
    }

    private String buildReason(ItemDto lost, ItemDto found, int score) {
        List<String> reasons = new ArrayList<>();

        if (equalsIgnoreCase(lost.category(), found.category())) {
            reasons.add("same category");
        }

        if (equalsIgnoreCase(lost.location(), found.location())) {
            reasons.add("same location");
        }

        if (containsSimilarText(lost.title(), found.title())) {
            reasons.add("similar title");
        }

        if (containsSimilarText(lost.description(), found.description())) {
            reasons.add("similar description");
        }

        if (reasons.isEmpty()) {
            return "Possible weak match. Score: " + score;
        }

        return "Matched by " + String.join(", ", reasons) + ". Score: " + score;
    }

    private boolean equalsIgnoreCase(String a, String b) {
        if (a == null || b == null) {
            return false;
        }

        return a.trim().equalsIgnoreCase(b.trim());
    }

    private boolean containsSimilarText(String a, String b) {
        if (a == null || b == null) {
            return false;
        }

        String first = a.toLowerCase();
        String second = b.toLowerCase();

        for (String word : first.split("\\s+")) {
            if (word.length() >= 4 && second.contains(word)) {
                return true;
            }
        }

        for (String word : second.split("\\s+")) {
            if (word.length() >= 4 && first.contains(word)) {
                return true;
            }
        }

        return false;
    }
}
