package com.trading.simulator.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "stock_symbol"})
})
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "stock_symbol")
    private String stockSymbol;

    private Integer quantity = 0;

    @Column(name = "average_price", precision = 10, scale = 2)
    private BigDecimal averagePrice;

    @Column(name = "total_invested", precision = 12, scale = 2)
    private BigDecimal totalInvested;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    // Constructors
    public Portfolio() {}

    public Portfolio(Long userId, String stockSymbol, Integer quantity, 
                    BigDecimal averagePrice, BigDecimal totalInvested) {
        this.userId = userId;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
        this.totalInvested = totalInvested;
        this.lastUpdated = LocalDateTime.now();
    }

    // Lifecycle methods
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getStockSymbol() { return stockSymbol; }
    public void setStockSymbol(String stockSymbol) { this.stockSymbol = stockSymbol; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getAveragePrice() { return averagePrice; }
    public void setAveragePrice(BigDecimal averagePrice) { this.averagePrice = averagePrice; }

    public BigDecimal getTotalInvested() { return totalInvested; }
    public void setTotalInvested(BigDecimal totalInvested) { this.totalInvested = totalInvested; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}