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

    @Transactional
    public Transaction executeTrade(TradeRequest tradeRequest) {
        User user = getUserOrThrow(tradeRequest.getUserId());
        Stock stock = getStockOrThrow(tradeRequest.getStockSymbol());
        BigDecimal price = stock.getCurrentPrice();
        BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(tradeRequest.getQuantity()));
        String type = tradeRequest.getTransactionType().toUpperCase();

        return switch (type) {
            case "BUY" -> executeBuyOrder(user, stock, tradeRequest.getQuantity(), price, totalAmount);
            case "SELL" -> executeSellOrder(user, stock, tradeRequest.getQuantity(), price, totalAmount);
            default -> throw new RuntimeException("Invalid transaction type. Must be BUY or SELL");
        };
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Stock getStockOrThrow(String symbol) {
        return stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found"));
    }

    private Transaction createAndSaveTransaction(Long userId, String symbol, String type,
                                                 Integer quantity, BigDecimal price, BigDecimal totalAmount) {
        return transactionRepository.save(new Transaction(userId, symbol, type, quantity, price, totalAmount));
    }

    private Transaction executeBuyOrder(User user, Stock stock, Integer quantity, BigDecimal price, BigDecimal totalAmount) {
        if (user.getBalance().compareTo(totalAmount) < 0) {
            throw new RuntimeException("Insufficient balance for this purchase");
        }

        user.setBalance(user.getBalance().subtract(totalAmount));
        userRepository.save(user);

        Transaction transaction = createAndSaveTransaction(user.getId(), stock.getSymbol(), "BUY", quantity, price, totalAmount);
        updatePortfolioForBuy(user.getId(), stock.getSymbol(), quantity, price, totalAmount);

        return transaction;
    }

    private Transaction executeSellOrder(User user, Stock stock, Integer quantity, BigDecimal price, BigDecimal totalAmount) {
        Portfolio portfolio = portfolioRepository.findByUserIdAndStockSymbol(user.getId(), stock.getSymbol())
                .orElseThrow(() -> new RuntimeException("Insufficient shares to sell"));

        if (portfolio.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient shares to sell");
        }

        user.setBalance(user.getBalance().add(totalAmount));
        userRepository.save(user);

        Transaction transaction = createAndSaveTransaction(user.getId(), stock.getSymbol(), "SELL", quantity, price, totalAmount);
        updatePortfolioForSell(portfolio, quantity);

        return transaction;
    }

    private void updatePortfolioForBuy(Long userId, String stockSymbol, Integer quantity, BigDecimal price, BigDecimal totalAmount) {
        Optional<Portfolio> portfolioOpt = portfolioRepository.findByUserIdAndStockSymbol(userId, stockSymbol);

        if (portfolioOpt.isPresent()) {
            Portfolio portfolio = portfolioOpt.get();
            BigDecimal newTotalInvested = portfolio.getTotalInvested().add(totalAmount);
            Integer newQuantity = portfolio.getQuantity() + quantity;
            BigDecimal newAvgPrice = newTotalInvested.divide(BigDecimal.valueOf(newQuantity), 2, RoundingMode.HALF_UP);

            portfolio.setQuantity(newQuantity);
            portfolio.setAveragePrice(newAvgPrice);
            portfolio.setTotalInvested(newTotalInvested);
            portfolioRepository.save(portfolio);
        } else {
            Portfolio portfolio = new Portfolio(userId, stockSymbol, quantity, price, totalAmount);
            portfolioRepository.save(portfolio);
        }
    }

    private void updatePortfolioForSell(Portfolio portfolio, Integer quantity) {
        Integer newQuantity = portfolio.getQuantity() - quantity;

        if (newQuantity == 0) {
            portfolioRepository.delete(portfolio);
        } else {
            BigDecimal soldInvestment = portfolio.getAveragePrice().multiply(BigDecimal.valueOf(quantity));
            portfolio.setQuantity(newQuantity);
            portfolio.setTotalInvested(portfolio.getTotalInvested().subtract(soldInvestment));
            portfolioRepository.save(portfolio);
        }
    }

    public boolean canExecuteTrade(TradeRequest tradeRequest) {
        User user = userRepository.findById(tradeRequest.getUserId()).orElse(null);
        if (user == null) return false;

        Stock stock = stockRepository.findBySymbol(tradeRequest.getStockSymbol().toUpperCase()).orElse(null);
        if (stock == null) return false;

        BigDecimal totalAmount = stock.getCurrentPrice().multiply(BigDecimal.valueOf(tradeRequest.getQuantity()));
        String type = tradeRequest.getTransactionType().toUpperCase();

        return switch (type) {
            case "BUY" -> user.getBalance().compareTo(totalAmount) >= 0;
            case "SELL" -> portfolioRepository.findByUserIdAndStockSymbol(user.getId(), stock.getSymbol())
                    .map(p -> p.getQuantity() >= tradeRequest.getQuantity())
                    .orElse(false);
            default -> false;
        };
    }
}
