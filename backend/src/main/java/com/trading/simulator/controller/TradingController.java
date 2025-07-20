package com.trading.simulator.controller;

import com.trading.simulator.dto.TradeRequest;
import com.trading.simulator.entity.Transaction;
import com.trading.simulator.service.TradingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trades")
@CrossOrigin(origins = "http://localhost:3000")
public class TradingController {

    @Autowired
    private TradingService tradingService;

    @PostMapping
    public ResponseEntity<?> executeTrade(@Valid @RequestBody TradeRequest tradeRequest) {
        try {
            Transaction transaction = tradingService.executeTrade(tradeRequest);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateTrade(@Valid @RequestBody TradeRequest tradeRequest) {
        try {
            boolean canExecute = tradingService.canExecuteTrade(tradeRequest);
            return ResponseEntity.ok(canExecute);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(false); 
        }
    }
}