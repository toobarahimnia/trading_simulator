package com.trading.simulator.controller;

import com.trading.simulator.dto.StockQuote;
import com.trading.simulator.entity.Stock;
import com.trading.simulator.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stocks")
@CrossOrigin(origins = "http://localhost:3000")
public class StockController {

    @Autowired
    private StockService stockService;

    // Get all available stocks
    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = stockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }

    // Get a stock by its symbol
    @GetMapping("/{symbol}")
    public ResponseEntity<Stock> getStock(@PathVariable String symbol) {
        Optional<Stock> optionalStock = stockService.getStockBySymbol(symbol);

        if (optionalStock.isPresent()) {
            Stock stock = optionalStock.get();
            return ResponseEntity.ok(stock);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get simulated stock quote
    @GetMapping("/{symbol}/quote")
    public ResponseEntity<StockQuote> getStockQuote(@PathVariable String symbol) {
        try {
            StockQuote quote = stockService.getStockQuote(symbol);
            return ResponseEntity.ok(quote);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get real-time stock quote
    @GetMapping("/{symbol}/realtime")
    public ResponseEntity<StockQuote> getRealTimeQuote(@PathVariable String symbol) {
        try {
            StockQuote quote = stockService.getRealTimeQuote(symbol);
            return ResponseEntity.ok(quote);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Check if a stock symbol exists in the system
    @GetMapping("/{symbol}/exists")
    public ResponseEntity<Boolean> stockExists(@PathVariable String symbol) {
        boolean exists = stockService.stockExists(symbol);
        return ResponseEntity.ok(exists);
    }
}
