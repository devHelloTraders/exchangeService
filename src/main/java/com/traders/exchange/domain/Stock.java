package com.traders.exchange.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "stock_details", uniqueConstraints = @UniqueConstraint(columnNames = {"symbol", "exchange"}))
@IdClass(StockId.class)
public class Stock extends AbstractAuditingEntity<Long> implements Serializable {

    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    private Double lastKnownPrice;
    @Transient
    private String currentPriceKey;

    @Column(name = "instrument_token", nullable = false)
    private long instrumentToken;

    @Column(name = "exchange_token", nullable = false)
    private long exchangeToken;

    @Id
    @Column(name = "symbol", nullable = false)
    private String tradingSymbol;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "last_price")
    private double lastPrice;

    @Column(name = "tick_size")
    private double tickSize;

    @Column(name = "instrument_type")
    private String instrumentType;

    @Column(name = "segment")
    private String segment;

    @Id
    @Column(name = "exchange", nullable = false)
    private String exchange;

    @Column(name = "strike")
    private String strike;

    @Column(name = "lot_size")
    private float lotSize;

    @Column(name = "expiry", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date expiry;


    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    private Set<Transaction> transactions;
    @Column(name = "isActive")
    private Boolean isActive= true;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTradingSymbol() {
        return tradingSymbol;
    }

    public void setTradingSymbol(String tradingSymbol) {
        this.tradingSymbol = tradingSymbol;
    }

    public Double getLastKnownPrice() {
        return lastKnownPrice;
    }

    public void setLastKnownPrice(Double lastKnownPrice) {
        this.lastKnownPrice = lastKnownPrice;
    }

    public String getCurrentPriceKey() {
        return getTradingSymbol()+":"+getExchange();
    }

    public long getInstrumentToken() {
        return instrumentToken;
    }

    public long getExchangeToken() {
        return exchangeToken;
    }

    public String getName() {
        return name;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public double getTickSize() {
        return tickSize;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public String getSegment() {
        return segment;
    }

    public String getExchange() {
        return exchange;
    }

    public String getStrike() {
        return strike;
    }

    public float getLotSize() {
        return lotSize;
    }

    public Date getExpiry() {
        return expiry;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setInstrumentToken(long instrumentToken) {
        this.instrumentToken = instrumentToken;
    }

    public void setExchangeToken(long exchangeToken) {
        this.exchangeToken = exchangeToken;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public void setTickSize(double tickSize) {
        this.tickSize = tickSize;
    }

    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public void setStrike(String strike) {
        this.strike = strike;
    }

    public void setLotSize(float lotSize) {
        this.lotSize = lotSize;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }


    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
