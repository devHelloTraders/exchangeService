package com.traders.exchange.repository;

import com.traders.exchange.domain.OrderCategory;
import com.traders.exchange.domain.Transaction;
import com.traders.exchange.domain.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    List<Transaction> findByCreatedBy (String userId);
    List<Transaction>findByTransactionStatusAndOrderCategory(TransactionStatus status, OrderCategory category);
}
