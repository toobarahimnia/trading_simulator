package com.trading.simulator.service;

import com.trading.simulator.dto.PortfolioSummary;
import com.trading.simulator.dto.StockQuote;
import com.trading.simulator.entity.Portfolio;
import com.trading.simulator.entity.Stock;
import com.trading.simulator.repository.PortfolioRepository;
import com.trading.simulator.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockService stockService;

    public List<PortfolioSummary> getUserPortfolio(Long userId) {
        List<Portfolio> portfolios = portfolioRepository.findByUserIdAndQuantityGreaterThan(userId, 0);
        List<PortfolioSummary> summaries = new ArrayList<>();

        for (Portfolio portfolio : portfolios) {
            stockRepository.findBySymbol(portfolio.getStockSymbol()).ifPresent(stock -> {
                StockQuote quote = stockService.getStockQuote(stock.getSymbol());
                PortfolioSummary summary = buildPortfolioSummary(portfolio, stock, quote);
                summaries.add(summary);
            });
        }

        return summaries;
    }

    public BigDecimal getTotalPortfolioValue(Long userId) {
        return getUserPortfolio(userId).stream()
                .map(PortfolioSummary::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalGainLoss(Long userId) {
        return getUserPortfolio(userId).stream()
                .map(PortfolioSummary::getGainLoss)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalGainLossPercent(Long userId) {
        BigDecimal totalInvested = getTotalInvested(userId);
        BigDecimal totalGainLoss = getTotalGainLoss(userId);

        return calculateGainLossPercent(totalInvested, totalGainLoss);
    }

    public BigDecimal getTotalInvested(Long userId) {
        return portfolioRepository.findByUserId(userId).stream()
                .map(Portfolio::getTotalInvested)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Optional<Portfolio> getPortfolioPosition(Long userId, String stockSymbol) {
        return portfolioRepository.findByUserIdAndStockSymbol(userId, stockSymbol);
    }

    public boolean hasPosition(Long userId, String stockSymbol) {
        return getPortfolioPosition(userId, stockSymbol)
                .map(p -> p.getQuantity() > 0)
                .orElse(false);
    }

    public Integer getAvailableShares(Long userId, String stockSymbol) {
        return getPortfolioPosition(userId, stockSymbol)
                .map(Portfolio::getQuantity)
                .orElse(0);
    }

    // ========== PRIVATE HELPERS ==========

    private PortfolioSummary buildPortfolioSummary(Portfolio portfolio, Stock stock, StockQuote quote) {
        BigDecimal currentValue = quote.getPrice().multiply(BigDecimal.valueOf(portfolio.getQuantity()));
        BigDecimal gainLoss = currentValue.subtract(portfolio.getTotalInvested());
        BigDecimal gainLossPercent = calculateGainLossPercent(portfolio.getTotalInvested(), gainLoss);

        return new PortfolioSummary(
                stock.getSymbol(),
                stock.getCompanyName(),
                portfolio.getQuantity(),
                portfolio.getAveragePrice(),
                quote.getPrice(),
                portfolio.getTotalInvested(),
                currentValue,
                gainLoss,
                gainLossPercent
        );
    }

    private BigDecimal calculateGainLossPercent(BigDecimal totalInvested, BigDecimal gainLoss) {
        if (totalInvested.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return gainLoss.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }
}
