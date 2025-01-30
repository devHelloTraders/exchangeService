package com.traders.exchange.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.traders.exchange.domain.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="transaction")
@Getter
@Setter
public class Transaction extends AbstractAuditingEntity<Long> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double price;
    private LocalDateTime requestTimestamp;
    private LocalDateTime completedTimestamp;
    private Double lotSize;
    private Double executedPrice;
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    private OrderCategory orderCategory;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "portfoliostock_id", updatable = false) // Read-only mapping
    private PortfolioStock portfolioStock;

    @ManyToOne
    @JoinColumn(name = "parent_transaction_id",referencedColumnName = "id")
    @JsonBackReference
    private Transaction parentTransaction;

    @OneToMany(mappedBy = "parentTransaction")
    @JsonManagedReference
    private List<Transaction> childTransactions;

    private int deleteflag;
    private double margin;
}