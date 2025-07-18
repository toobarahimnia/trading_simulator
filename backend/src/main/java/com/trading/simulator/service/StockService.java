package com.trading.simulator.service;

import com.trading.simulator.dto.StockQuote;
import com.trading.simulator.entity.Stock;
import com.trading.simulator.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Optional<Stock> getStockBySymbol(String symbol) {
        return stockRepository.findBySymbol(symbol.toUpperCase());
    }

    public StockQuote getStockQuote(String symbol) {
        return stockRepository.findBySymbol(symbol.toUpperCase())
                .map(this::buildSimulatedQuote)
                .orElseGet(() -> createDefaultStockQuote(symbol));
    }

    public StockQuote getRealTimeQuote(String symbol) {
        return getStockQuote(symbol); // For demo, just simulate
    }

    public Stock saveStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public void updateStockPrice(String symbol, BigDecimal newPrice) {
        stockRepository.findBySymbol(symbol.toUpperCase())
                .ifPresent(stock -> {
                    stock.setCurrentPrice(newPrice);
                    stockRepository.save(stock);
                });
    }

    public boolean stockExists(String symbol) {
        return stockRepository.existsBySymbol(symbol.toUpperCase());
    }

    // ========== PRIVATE HELPERS ==========

    private StockQuote buildSimulatedQuote(Stock stock) {
        BigDecimal currentPrice = stock.getCurrentPrice();
        BigDecimal change = generateRandomChange(currentPrice);
        BigDecimal newPrice = currentPrice.add(change);
        BigDecimal changePercent = calculateChangePercent(currentPrice, change);

        stock.setCurrentPrice(newPrice);
        stockRepository.save(stock);

        return new StockQuote(
                stock.getSymbol(),
                stock.getCompanyName(),
                newPrice,
                change,
                changePercent,
                stock.getLastUpdated().toString()
        );
    }

    private BigDecimal generateRandomChange(BigDecimal currentPrice) {
        double randomPercent = (Math.random() - 0.5) * 0.04; // -2% to +2%
        return currentPrice.multiply(BigDecimal.valueOf(randomPercent));
    }

    private BigDecimal calculateChangePercent(BigDecimal originalPrice, BigDecimal change) {
        if (originalPrice.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return change.divide(originalPrice, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
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
}
