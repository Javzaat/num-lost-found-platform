package mn.edu.num.lostfound.claim.service;

import mn.edu.num.lostfound.claim.dto.ClaimResponse;
import mn.edu.num.lostfound.claim.dto.CreateClaimRequest;
import mn.edu.num.lostfound.claim.entity.Claim;
import mn.edu.num.lostfound.claim.entity.ClaimStatus;
import mn.edu.num.lostfound.claim.repository.ClaimRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;

    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public ClaimResponse createClaim(CreateClaimRequest request) {
        validateCreateRequest(request);

        Claim claim = new Claim(
                request.getLostItemId(),
                request.getFoundItemId(),
                request.getClaimantName(),
                request.getClaimantEmail(),
                request.getProofDescription()
        );

        return toResponse(claimRepository.save(claim));
    }

    public List<ClaimResponse> getAllClaims() {
        return claimRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ClaimResponse> getClaimsByStatus(ClaimStatus status) {
        return claimRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ClaimResponse getClaimById(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found: " + id));

        return toResponse(claim);
    }

    public ClaimResponse updateClaimStatus(Long id, ClaimStatus status) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found: " + id));

        claim.setStatus(status);

        return toResponse(claimRepository.save(claim));
    }

    private void validateCreateRequest(CreateClaimRequest request) {
        if (request.getLostItemId() == null) {
            throw new IllegalArgumentException("lostItemId is required");
        }

        if (request.getFoundItemId() == null) {
            throw new IllegalArgumentException("foundItemId is required");
        }

        if (isBlank(request.getClaimantName())) {
            throw new IllegalArgumentException("claimantName is required");
        }

        if (isBlank(request.getClaimantEmail())) {
            throw new IllegalArgumentException("claimantEmail is required");
        }

        if (isBlank(request.getProofDescription())) {
            throw new IllegalArgumentException("proofDescription is required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private ClaimResponse toResponse(Claim claim) {
        return new ClaimResponse(
                claim.getId(),
                claim.getLostItemId(),
                claim.getFoundItemId(),
                claim.getClaimantName(),
                claim.getClaimantEmail(),
                claim.getProofDescription(),
                claim.getStatus(),
                claim.getCreatedAt()
        );
    }
}
