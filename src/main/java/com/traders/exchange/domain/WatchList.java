package com.traders.exchange.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "watchlist")
//have constraints on DB side
public class WatchList extends AbstractAuditingEntity<Long> implements Serializable {

    public WatchList(){

    }
    public WatchList(Long userId){
        this.userId=userId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long userId;

    @OneToMany(mappedBy = "watchList", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNum ASC")
    private List<WatchlistStock> stocks = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<WatchlistStock> getStocks() {
        return stocks;
    }

    public void setStocks(List<WatchlistStock> stocks) {
        this.stocks = stocks;
    }
}
