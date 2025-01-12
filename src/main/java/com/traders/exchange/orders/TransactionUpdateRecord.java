package com.traders.exchange.orders;

import com.traders.exchange.domain.TransactionStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
public record TransactionUpdateRecord(
        Long id,
        Double price,
        TransactionStatus transactionStatus
)
{


}
