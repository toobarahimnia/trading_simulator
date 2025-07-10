package com.trading.simulator.service;

import com.trading.simulator.dto.PortfolioSummary;
import com.trading.simulator.dto.StockQuote;
import com.trading.simulator.entity.Portfolio;
import com.trading.simulator.entity.Stock;
import com.trading.simulator.entity.Transaction;
import com.trading.simulator.repository.PortfolioRepository;
import com.trading.simulator.repository.StockRepository;
import com.trading.simulator.repository.TransactionRepository;
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
    private TransactionRepository transactionRepository;

    @Autowired
    private StockService stockService;

    public List<PortfolioSummary> getUserPortfolio(Long userId) {
        List<Portfolio> portfolios = portfolioRepository.findByUserIdAndQuantityGreaterThan(userId, 0);
        List<PortfolioSummary> summaries = new ArrayList<>();

        for (Portfolio portfolio : portfolios) {
            Optional<Stock> stockOpt = stockRepository.findBySymbol(portfolio.getStockSymbol());
            
            if (stockOpt.isPresent()) {
                Stock stock = stockOpt.get();
                StockQuote quote = stockService.getStockQuote(stock.getSymbol());
                
                BigDecimal currentValue = quote.getPrice().multiply(BigDecimal.valueOf(portfolio.getQuantity()));
                BigDecimal gainLoss = currentValue.subtract(portfolio.getTotalInvested());
                BigDecimal gainLossPercent = portfolio.getTotalInvested().compareTo(BigDecimal.ZERO) > 0 
                    ? gainLoss.divide(portfolio.getTotalInvested(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;

                PortfolioSummary summary = new PortfolioSummary(
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
                
                summaries.add(summary);
            }
        }

        return summaries;
    }

    public BigDecimal getTotalPortfolioValue(Long userId) {
        List<PortfolioSummary> portfolio = getUserPortfolio(userId);
        return portfolio.stream()
                .map(PortfolioSummary::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalGainLoss(Long userId) {
        List<PortfolioSummary> portfolio = getUserPortfolio(userId);
        return portfolio.stream()
                .map(PortfolioSummary::getGainLoss)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalGainLossPercent(Long userId) {
        BigDecimal totalInvested = getTotalInvested(userId);
        BigDecimal totalGainLoss = getTotalGainLoss(userId);
        
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            return totalGainLoss.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }
        
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalInvested(Long userId) {
        List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);
        return portfolios.stream()
                .map(Portfolio::getTotalInvested)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Transaction> getUserTransactionHistory(Long userId) {
        return transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
    }

    public List<Transaction> getStockTransactionHistory(Long userId, String stockSymbol) {
        return transactionRepository.findByUserIdAndStockSymbolOrderByTransactionDateDesc(userId, stockSymbol);
    }

    public Optional<Portfolio> getPortfolioPosition(Long userId, String stockSymbol) {
        return portfolioRepository.findByUserIdAndStockSymbol(userId, stockSymbol);
    }

    public boolean hasPosition(Long userId, String stockSymbol) {
        Optional<Portfolio> portfolio = portfolioRepository.findByUserIdAndStockSymbol(userId, stockSymbol);
        return portfolio.isPresent() && portfolio.get().getQuantity() > 0;
    }

    public Integer getAvailableShares(Long userId, String stockSymbol) {
        Optional<Portfolio> portfolio = portfolioRepository.findByUserIdAndStockSymbol(userId, stockSymbol);
        return portfolio.map(Portfolio::getQuantity).orElse(0);
    }
}