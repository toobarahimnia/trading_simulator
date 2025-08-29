package com.trading.simulator.controller;

import com.trading.simulator.dto.TradeRequest;
import com.trading.simulator.entity.Transaction;
import com.trading.simulator.service.TradingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trades")
@CrossOrigin(origins = "http://localhost:3000")
public class TradingController {

    private final TradingService tradingService;

    public TradingController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    @PostMapping
    public ResponseEntity<Transaction> executeTrade(@Valid @RequestBody TradeRequest tradeRequest) { 
        Transaction transaction = tradingService.executeTrade(tradeRequest);
        return ResponseEntity.ok(transaction);  
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateTrade(@Valid @RequestBody TradeRequest tradeRequest) {
        boolean canExecute = tradingService.canExecuteTrade(tradeRequest);
        return ResponseEntity.ok(canExecute);
    }
}