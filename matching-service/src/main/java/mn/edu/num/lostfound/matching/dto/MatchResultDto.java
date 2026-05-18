package mn.edu.num.lostfound.matching.dto;

public record MatchResultDto(
        Long lostItemId,
        Long foundItemId,
        String lostItemTitle,
        String foundItemTitle,
        int score,
        String reason
) {
}
