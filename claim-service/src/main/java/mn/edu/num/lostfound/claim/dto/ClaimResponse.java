package mn.edu.num.lostfound.claim.dto;

import mn.edu.num.lostfound.claim.entity.ClaimStatus;

import java.time.LocalDateTime;

public class ClaimResponse {

    private Long id;
    private Long lostItemId;
    private Long foundItemId;
    private String claimantName;
    private String claimantEmail;
    private String proofDescription;
    private ClaimStatus status;
    private LocalDateTime createdAt;

    public ClaimResponse(Long id, Long lostItemId, Long foundItemId,
                         String claimantName, String claimantEmail,
                         String proofDescription, ClaimStatus status,
                         LocalDateTime createdAt) {
        this.id = id;
        this.lostItemId = lostItemId;
        this.foundItemId = foundItemId;
        this.claimantName = claimantName;
        this.claimantEmail = claimantEmail;
        this.proofDescription = proofDescription;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getLostItemId() {
        return lostItemId;
    }

    public Long getFoundItemId() {
        return foundItemId;
    }

    public String getClaimantName() {
        return claimantName;
    }

    public String getClaimantEmail() {
        return claimantEmail;
    }

    public String getProofDescription() {
        return proofDescription;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
