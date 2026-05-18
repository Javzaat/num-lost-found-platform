package mn.edu.num.lostfound.claim.repository;

import mn.edu.num.lostfound.claim.entity.Claim;
import mn.edu.num.lostfound.claim.entity.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByStatus(ClaimStatus status);

    List<Claim> findByLostItemId(Long lostItemId);

    List<Claim> findByFoundItemId(Long foundItemId);
}
