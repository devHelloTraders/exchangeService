package com.traders.exchange.service;

import com.traders.common.utils.CommonValidations;
import com.traders.exchange.domain.Transaction;
import com.traders.exchange.domain.TransactionStatus;
import com.traders.exchange.exception.BadRequestAlertException;
import com.traders.exchange.repository.TransactionRepository;
import com.traders.exchange.service.dto.TransactionDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;

    public TransactionService(TransactionRepository transactionRepository,  ModelMapper modelMapper) {
        this.transactionRepository = transactionRepository;
        this.modelMapper = modelMapper;
    }

}
