package mn.edu.num.lostfound.matching.controller;

import mn.edu.num.lostfound.matching.dto.MatchResultDto;
import mn.edu.num.lostfound.matching.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping
    public ResponseEntity<List<MatchResultDto>> findAllMatches() {
        return ResponseEntity.ok(matchingService.findMatches());
    }

    @GetMapping("/lost/{lostItemId}")
    public ResponseEntity<List<MatchResultDto>> findMatchesForLostItem(@PathVariable Long lostItemId) {
        return ResponseEntity.ok(matchingService.findMatchesForLostItem(lostItemId));
    }
}
