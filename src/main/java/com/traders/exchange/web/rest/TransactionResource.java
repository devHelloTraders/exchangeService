package com.traders.exchange.web.rest;

import com.traders.exchange.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class TransactionResource {


    private static final Logger LOG = LoggerFactory.getLogger(TransactionResource.class);
    private final TransactionService transactionService;
    @Value("${config.clientApp.name}")
    private String applicationName;

    public TransactionResource(TransactionService transactionService) {

        this.transactionService = transactionService;
    }

}
