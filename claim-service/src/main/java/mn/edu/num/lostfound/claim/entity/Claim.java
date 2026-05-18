package mn.edu.num.lostfound.claim.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long lostItemId;
    private Long foundItemId;

    private String claimantName;
    private String claimantEmail;

    @Column(length = 1000)
    private String proofDescription;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    private LocalDateTime createdAt;

    public Claim() {
    }

    public Claim(Long lostItemId, Long foundItemId, String claimantName,
                 String claimantEmail, String proofDescription) {
        this.lostItemId = lostItemId;
        this.foundItemId = foundItemId;
        this.claimantName = claimantName;
        this.claimantEmail = claimantEmail;
        this.proofDescription = proofDescription;
        this.status = ClaimStatus.PENDING;
        this.createdAt = LocalDateTime.now();
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

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }
}
