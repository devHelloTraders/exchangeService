package com.traders.exchange.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "stock_details", uniqueConstraints = @UniqueConstraint(columnNames = {"symbol", "exchange","instrument_token"}))
@IdClass(StockId.class)
@Getter
@Setter
public class Stock extends AbstractAuditingEntity<Long> implements Serializable {

    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    private Double lastKnownPrice;
    @Transient
    private String currentPriceKey;
    @Id
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

    @Column(name="exchange_segment")
    private String exchangeSegment;

//    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
//    private Set<Transaction> transactions;
    @Column(name = "isActive")
    private Boolean isActive= true;


}
