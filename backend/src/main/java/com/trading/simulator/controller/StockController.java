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

    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = stockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<Stock> getStock(@PathVariable String symbol) {
        Optional<Stock> stock = stockService.getStockBySymbol(symbol);
        return stock.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{symbol}/quote")
    public ResponseEntity<StockQuote> getStockQuote(@PathVariable String symbol) {
        try {
            StockQuote quote = stockService.getStockQuote(symbol);
            return ResponseEntity.ok(quote);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{symbol}/realtime")
    public ResponseEntity<StockQuote> getRealTimeQuote(@PathVariable String symbol) {
        try {
            StockQuote quote = stockService.getRealTimeQuote(symbol);
            return ResponseEntity.ok(quote);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{symbol}/exists")
    public ResponseEntity<Boolean> stockExists(@PathVariable String symbol) {
        boolean exists = stockService.stockExists(symbol);
        return ResponseEntity.ok(exists);
    }
}