package mn.edu.num.lostfound.claim.dto;

public class CreateClaimRequest {

    private Long lostItemId;
    private Long foundItemId;
    private String claimantName;
    private String claimantEmail;
    private String proofDescription;

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

    public void setLostItemId(Long lostItemId) {
        this.lostItemId = lostItemId;
    }

    public void setFoundItemId(Long foundItemId) {
        this.foundItemId = foundItemId;
    }

    public void setClaimantName(String claimantName) {
        this.claimantName = claimantName;
    }

    public void setClaimantEmail(String claimantEmail) {
        this.claimantEmail = claimantEmail;
    }

    public void setProofDescription(String proofDescription) {
        this.proofDescription = proofDescription;
    }
}
