package co.mvpmatch.web.rest;

import co.mvpmatch.service.BuyerService;
import co.mvpmatch.service.dto.BuyResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static co.mvpmatch.security.AuthoritiesConstants.BUYER;

/**
 * REST controller for managing Vending Machine specific operations
 */

@RestController
@RequestMapping("/api")
@Transactional
public class BuyerController {

    private final BuyerService buyerService;

    public BuyerController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @PostMapping("/buy")
    @PreAuthorize("hasAuthority(\"" + BUYER + "\")")
    public ResponseEntity<BuyResponse> buy(
        @RequestParam Long productId,
        @RequestParam Integer quantity,
        Authentication authentication) {
        String userName = authentication.getName();

        return ResponseEntity.ok(buyerService.buy(userName, productId, quantity));
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasAuthority(\"" + BUYER + "\")")
    public ResponseEntity deposit(
        @RequestParam Integer coin,
        @RequestParam Integer nr,
        Authentication authentication) {

        String userName = authentication.getName();
        buyerService.deposit(userName, coin, nr);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset")
    @PreAuthorize("hasAuthority(\"" + BUYER + "\")")
    public ResponseEntity<Map<Integer, Integer>> reset(Authentication authentication) {

        String userName = authentication.getName();
        return ResponseEntity.ok(buyerService.resetDeposit(userName));
    }

}
