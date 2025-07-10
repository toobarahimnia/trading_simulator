package com.trading.simulator.controller;

import com.trading.simulator.dto.PortfolioSummary;
import com.trading.simulator.entity.Portfolio;
import com.trading.simulator.entity.Transaction;
import com.trading.simulator.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/portfolio")
@CrossOrigin(origins = "http://localhost:3000")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PortfolioSummary>> getUserPortfolio(@PathVariable Long userId) {
        List<PortfolioSummary> portfolio = portfolioService.getUserPortfolio(userId);
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping("/user/{userId}/value")
    public ResponseEntity<BigDecimal> getTotalPortfolioValue(@PathVariable Long userId) {
        BigDecimal totalValue = portfolioService.getTotalPortfolioValue(userId);
        return ResponseEntity.ok(totalValue);
    }

    @GetMapping("/user/{userId}/gainloss")
    public ResponseEntity<BigDecimal> getTotalGainLoss(@PathVariable Long userId) {
        BigDecimal totalGainLoss = portfolioService.getTotalGainLoss(userId);
        return ResponseEntity.ok(totalGainLoss);
    }

    @GetMapping("/user/{userId}/gainloss-percent")
    public ResponseEntity<BigDecimal> getTotalGainLossPercent(@PathVariable Long userId) {
        BigDecimal totalGainLossPercent = portfolioService.getTotalGainLossPercent(userId);
        return ResponseEntity.ok(totalGainLossPercent);
    }

    @GetMapping("/user/{userId}/invested")
    public ResponseEntity<BigDecimal> getTotalInvested(@PathVariable Long userId) {
        BigDecimal totalInvested = portfolioService.getTotalInvested(userId);
        return ResponseEntity.ok(totalInvested);
    }

    @GetMapping("/user/{userId}/transactions")
    public ResponseEntity<List<Transaction>> getUserTransactionHistory(@PathVariable Long userId) {
        List<Transaction> transactions = portfolioService.getUserTransactionHistory(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/user/{userId}/transactions/{stockSymbol}")
    public ResponseEntity<List<Transaction>> getStockTransactionHistory(@PathVariable Long userId, @PathVariable String stockSymbol) {
        List<Transaction> transactions = portfolioService.getStockTransactionHistory(userId, stockSymbol);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/user/{userId}/position/{stockSymbol}")
    public ResponseEntity<Portfolio> getPortfolioPosition(@PathVariable Long userId, @PathVariable String stockSymbol) {
        Optional<Portfolio> position = portfolioService.getPortfolioPosition(userId, stockSymbol);
        return position.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/shares/{stockSymbol}")
    public ResponseEntity<Integer> getAvailableShares(@PathVariable Long userId, @PathVariable String stockSymbol) {
        Integer shares = portfolioService.getAvailableShares(userId, stockSymbol);
        return ResponseEntity.ok(shares);
    }

    @GetMapping("/user/{userId}/has-position/{stockSymbol}")
    public ResponseEntity<Boolean> hasPosition(@PathVariable Long userId, @PathVariable String stockSymbol) {
        boolean hasPosition = portfolioService.hasPosition(userId, stockSymbol);
        return ResponseEntity.ok(hasPosition);
    }
}