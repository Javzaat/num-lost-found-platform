package mn.edu.num.lostfound.claim.controller;

import mn.edu.num.lostfound.claim.dto.ClaimResponse;
import mn.edu.num.lostfound.claim.dto.CreateClaimRequest;
import mn.edu.num.lostfound.claim.entity.ClaimStatus;
import mn.edu.num.lostfound.claim.service.ClaimService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClaimResponse createClaim(@RequestBody CreateClaimRequest request) {
        return claimService.createClaim(request);
    }

    @GetMapping
    public List<ClaimResponse> getAllClaims() {
        return claimService.getAllClaims();
    }

    @GetMapping("/{id}")
    public ClaimResponse getClaimById(@PathVariable Long id) {
        return claimService.getClaimById(id);
    }

    @GetMapping("/status/{status}")
    public List<ClaimResponse> getClaimsByStatus(@PathVariable ClaimStatus status) {
        return claimService.getClaimsByStatus(status);
    }

    @PatchMapping("/{id}/status/{status}")
    public ClaimResponse updateClaimStatus(@PathVariable Long id,
                                           @PathVariable ClaimStatus status) {
        return claimService.updateClaimStatus(id, status);
    }
}
