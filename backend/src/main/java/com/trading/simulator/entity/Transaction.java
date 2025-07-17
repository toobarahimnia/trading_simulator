package com.trading.simulator.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "stock_symbol")
    private String stockSymbol;

    @Column(name = "transaction_type")
    private String transactionType; // BUY or SELL

    private Integer quantity;

    @Column(name = "price_per_share", precision = 10, scale = 2)
    private BigDecimal pricePerShare;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    // Constructors
    public Transaction() {}

    public Transaction(Long userId, String stockSymbol, String transactionType, 
                      Integer quantity, BigDecimal pricePerShare, BigDecimal totalAmount) {
        this.userId = userId;
        this.stockSymbol = stockSymbol;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.totalAmount = totalAmount;
        this.transactionDate = LocalDateTime.now();
    }

    // Lifecycle methods
    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getStockSymbol() { return stockSymbol; }
    public void setStockSymbol(String stockSymbol) { this.stockSymbol = stockSymbol; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPricePerShare() { return pricePerShare; }
    public void setPricePerShare(BigDecimal pricePerShare) { this.pricePerShare = pricePerShare; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
}