package com.traders.exchange.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "watchlist_stock" , uniqueConstraints = {
        @UniqueConstraint(columnNames = {"watchlist_id", "stock_id"})})
public class WatchlistStock extends AbstractAuditingEntity<Long> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long currentPriceKey;
    private Long userId;
   private int orderNum;

    @OneToOne
    @JoinColumn(name = "stock_id", referencedColumnName = "id")
    //give manual foreign key
    Stock stock;

    @ManyToOne
    @JoinColumn(name = "watchlist_id")
    private WatchList watchList;
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Long getCurrentPriceKey() {
        return currentPriceKey;
    }

    public void setCurrentPriceKey(Long currentPriceKey) {
        this.currentPriceKey = currentPriceKey;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public WatchList getWatchList() {
        return watchList;
    }

    public void setWatchList(WatchList watchList) {
        this.watchList = watchList;
    }
}
