package com.cheburekmi.cheburek.controller;

import com.cheburekmi.cheburek.entity.LoyaltyCard;
import com.cheburekmi.cheburek.repository.LoyaltyCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
@Slf4j
public class LoyaltyController {

    private final LoyaltyCardRepository loyaltyCardRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<LoyaltyCardResponse> getLoyaltyCard(@PathVariable String userId) {
        log.info("Got request to get loyalty card for user {}", userId);
        
        LoyaltyCard card = loyaltyCardRepository.findByUserId(userId)
                .orElseGet(() -> {
                    LoyaltyCard newCard = new LoyaltyCard();
                    newCard.setUserId(userId);
                    newCard.setCurrent(0);
                    newCard.setTarget(10);
                    newCard.setFreeAvailable(0);
                    return loyaltyCardRepository.save(newCard);
                });
        
        return ResponseEntity.ok(new LoyaltyCardResponse(
                card.getId(),
                card.getUserId(),
                card.getCurrent(),
                card.getTarget(),
                card.getFreeAvailable()
        ));
    }

    public record LoyaltyCardResponse(
            Long id,
            String userId,
            Integer current,
            Integer target,
            Integer freeAvailable
    ) {}
}
