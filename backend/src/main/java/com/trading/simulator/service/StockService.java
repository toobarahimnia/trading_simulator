package com.trading.simulator.service;

import com.trading.simulator.dto.StockQuote;
import com.trading.simulator.entity.Stock;
import com.trading.simulator.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    
    @Value("${alphavantage.api.key}")
    private String apiKey;

    @Value("${alphavantage.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Optional<Stock> getStockBySymbol(String symbol) {
        return stockRepository.findBySymbol(symbol.toUpperCase());
    }

    public StockQuote getStockQuote(String symbol) {
        // First try to get from database
        Optional<Stock> stockOpt = stockRepository.findBySymbol(symbol.toUpperCase());
        
        if (stockOpt.isPresent()) {
            Stock stock = stockOpt.get();
            
            // For demo purposes, simulate real-time data with small random changes
            BigDecimal currentPrice = stock.getCurrentPrice();
            BigDecimal change = generateRandomChange(currentPrice);
            BigDecimal changePercent = change.divide(currentPrice, 4, BigDecimal.ROUND_HALF_UP)
                                          .multiply(BigDecimal.valueOf(100));
            
            // Update stock price in database
            stock.setCurrentPrice(currentPrice.add(change));
            stockRepository.save(stock);
            
            return new StockQuote(
                stock.getSymbol(),
                stock.getCompanyName(),
                currentPrice.add(change),
                change,
                changePercent,
                stock.getLastUpdated().toString()
            );
        }
        
        // If not found in database, create default response
        return createDefaultStockQuote(symbol);
    }

    public StockQuote getRealTimeQuote(String symbol) {
        try {
            // This would call Alpha Vantage API in production
            // For demo, we'll use database values with simulated changes
            return getStockQuote(symbol);
        } catch (Exception e) {
            // Fallback to database or default values
            return getStockQuote(symbol);
        }
    }

    public Stock saveStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public void updateStockPrice(String symbol, BigDecimal newPrice) {
        Optional<Stock> stockOpt = stockRepository.findBySymbol(symbol.toUpperCase());
        if (stockOpt.isPresent()) {
            Stock stock = stockOpt.get();
            stock.setCurrentPrice(newPrice);
            stockRepository.save(stock);
        }
    }

    private BigDecimal generateRandomChange(BigDecimal currentPrice) {
        // Generate random change between -2% to +2%
        double randomPercent = (Math.random() - 0.5) * 0.04; // -0.02 to +0.02
        return currentPrice.multiply(BigDecimal.valueOf(randomPercent));
    }

    private StockQuote createDefaultStockQuote(String symbol) {
        return new StockQuote(
            symbol.toUpperCase(),
            "Unknown Company",
            BigDecimal.valueOf(100.00),
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            "Unknown"
        );
    }

    public boolean stockExists(String symbol) {
        return stockRepository.existsBySymbol(symbol.toUpperCase());
    }
}