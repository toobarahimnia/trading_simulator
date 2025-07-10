package com.trading.simulator.repository;

import com.trading.simulator.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId);
    
    List<Transaction> findByUserIdAndStockSymbolOrderByTransactionDateDesc(Long userId, String stockSymbol);
    
    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.stockSymbol = :stockSymbol AND t.transactionType = :type ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserIdAndStockSymbolAndTransactionType(@Param("userId") Long userId, 
                                                                   @Param("stockSymbol") String stockSymbol, 
                                                                   @Param("type") String transactionType);
}