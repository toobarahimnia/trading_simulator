package com.trading.simulator.service;

import com.trading.simulator.dto.TradeRequest;
import com.trading.simulator.entity.Portfolio;
import com.trading.simulator.entity.Stock;
import com.trading.simulator.entity.Transaction;
import com.trading.simulator.entity.User;
import com.trading.simulator.repository.PortfolioRepository;
import com.trading.simulator.repository.StockRepository;
import com.trading.simulator.repository.TransactionRepository;
import com.trading.simulator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class TradingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private StockService stockService;

    @Transactional
    public Transaction executeTrade(TradeRequest tradeRequest) {
        // Validate user exists
        User user = userRepository.findById(tradeRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate stock exists
        Stock stock = stockRepository.findBySymbol(tradeRequest.getStockSymbol().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        BigDecimal currentPrice = stock.getCurrentPrice();
        BigDecimal totalAmount = currentPrice.multiply(BigDecimal.valueOf(tradeRequest.getQuantity()));

        Transaction transaction;

        if ("BUY".equalsIgnoreCase(tradeRequest.getTransactionType())) {
            transaction = executeBuyOrder(user, stock, tradeRequest.getQuantity(), currentPrice, totalAmount);
        } else if ("SELL".equalsIgnoreCase(tradeRequest.getTransactionType())) {
            transaction = executeSellOrder(user, stock, tradeRequest.getQuantity(), currentPrice, totalAmount);
        } else {
            throw new RuntimeException("Invalid transaction type. Must be BUY or SELL");
        }

        return transaction;
    }

    private Transaction executeBuyOrder(User user, Stock stock, Integer quantity, BigDecimal price, BigDecimal totalAmount) {
        // Check if user has sufficient balance
        if (user.getBalance().compareTo(totalAmount) < 0) {
            throw new RuntimeException("Insufficient balance for this purchase");
        }

        // Update user balance
        user.setBalance(user.getBalance().subtract(totalAmount));
        userRepository.save(user);

        // Create transaction record
        Transaction transaction = new Transaction(
                user.getId(),
                stock.getSymbol(),
                "BUY",
                quantity,
                price,
                totalAmount
        );
        transaction = transactionRepository.save(transaction);

        // Update portfolio
        updatePortfolioForBuy(user.getId(), stock.getSymbol(), quantity, price, totalAmount);

        return transaction;
    }

    private Transaction executeSellOrder(User user, Stock stock, Integer quantity, BigDecimal price, BigDecimal totalAmount) {
        // Check if user has sufficient shares
        Optional<Portfolio> portfolioOpt = portfolioRepository.findByUserIdAndStockSymbol(user.getId(), stock.getSymbol());
        
        if (portfolioOpt.isEmpty() || portfolioOpt.get().getQuantity() < quantity) {
            throw new RuntimeException("Insufficient shares to sell");
        }

        // Update user balance
        user.setBalance(user.getBalance().add(totalAmount));
        userRepository.save(user);

        // Create transaction record
        Transaction transaction = new Transaction(
                user.getId(),
                stock.getSymbol(),
                "SELL",
                quantity,
                price,
                totalAmount
        );
        transaction = transactionRepository.save(transaction);

        // Update portfolio
        updatePortfolioForSell(user.getId(), stock.getSymbol(), quantity, price, totalAmount);

        return transaction;
    }

    private void updatePortfolioForBuy(Long userId, String stockSymbol, Integer quantity, BigDecimal price, BigDecimal totalAmount) {
        Optional<Portfolio> portfolioOpt = portfolioRepository.findByUserIdAndStockSymbol(userId, stockSymbol);

        if (portfolioOpt.isPresent()) {
            // Update existing portfolio entry
            Portfolio portfolio = portfolioOpt.get();
            
            BigDecimal newTotalInvested = portfolio.getTotalInvested().add(totalAmount);
            Integer newQuantity = portfolio.getQuantity() + quantity;
            BigDecimal newAveragePrice = newTotalInvested.divide(BigDecimal.valueOf(newQuantity), 2, RoundingMode.HALF_UP);

            portfolio.setQuantity(newQuantity);
            portfolio.setAveragePrice(newAveragePrice);
            portfolio.setTotalInvested(newTotalInvested);
            
            portfolioRepository.save(portfolio);
        } else {
            // Create new portfolio entry
            Portfolio portfolio = new Portfolio(userId, stockSymbol, quantity, price, totalAmount);
            portfolioRepository.save(portfolio);
        }
    }

    private void updatePortfolioForSell(Long userId, String stockSymbol, Integer quantity, BigDecimal price, BigDecimal totalAmount) {
        Portfolio portfolio = portfolioRepository.findByUserIdAndStockSymbol(userId, stockSymbol)
                .orElseThrow(() -> new RuntimeException("Portfolio entry not found"));

        Integer newQuantity = portfolio.getQuantity() - quantity;
        
        if (newQuantity == 0) {
            // Remove portfolio entry if no shares left
            portfolioRepository.delete(portfolio);
        } else {
            // Update portfolio entry
            BigDecimal soldInvestment = portfolio.getAveragePrice().multiply(BigDecimal.valueOf(quantity));
            BigDecimal newTotalInvested = portfolio.getTotalInvested().subtract(soldInvestment);
            
            portfolio.setQuantity(newQuantity);
            portfolio.setTotalInvested(newTotalInvested);
            
            portfolioRepository.save(portfolio);
        }
    }

    public boolean canExecuteTrade(TradeRequest tradeRequest) {
        User user = userRepository.findById(tradeRequest.getUserId())
                .orElse(null);
        
        if (user == null) return false;

        Stock stock = stockRepository.findBySymbol(tradeRequest.getStockSymbol().toUpperCase())
                .orElse(null);
        
        if (stock == null) return false;

        BigDecimal totalAmount = stock.getCurrentPrice().multiply(BigDecimal.valueOf(tradeRequest.getQuantity()));

        if ("BUY".equalsIgnoreCase(tradeRequest.getTransactionType())) {
            return user.getBalance().compareTo(totalAmount) >= 0;
        } else if ("SELL".equalsIgnoreCase(tradeRequest.getTransactionType())) {
            Optional<Portfolio> portfolioOpt = portfolioRepository.findByUserIdAndStockSymbol(user.getId(), stock.getSymbol());
            return portfolioOpt.isPresent() && portfolioOpt.get().getQuantity() >= tradeRequest.getQuantity();
        }

        return false;
    }
}