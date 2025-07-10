package com.trading.simulator.repository;

import com.trading.simulator.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByUserId(Long userId);
    
    Optional<Portfolio> findByUserIdAndStockSymbol(Long userId, String stockSymbol);
    
    List<Portfolio> findByUserIdAndQuantityGreaterThan(Long userId, Integer quantity);
    
    void deleteByUserIdAndStockSymbol(Long userId, String stockSymbol);
}