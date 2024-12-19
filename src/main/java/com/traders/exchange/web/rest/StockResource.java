package com.traders.exchange.web.rest;

import com.traders.common.appconfig.util.PaginationUtil;
import com.traders.common.constants.AuthoritiesConstants;
import com.traders.exchange.service.StockService;
import com.traders.exchange.service.dto.StockDTO;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/stocks")
public class StockResource {
    private final StockService stockService;

    private static final List<String> ALLOWED_ORDERED_PROPERTIES = List.of("id", "tradingSymbol", "exchange", "name");
    @Value("${config.clientApp.name}")
    private String applicationName;

    public StockResource(StockService stockService) {
        this.stockService = stockService;
    }


    @GetMapping("/getAll")
    public ResponseEntity<List<StockDTO>> getAllStocks(@RequestParam @NotNull String exchange, @ParameterObject Pageable pageable) {
        log.debug("REST request to get all Stocks for given exchange");
        if (!onlyContainsAllowedProperties(pageable)) {
            return ResponseEntity.badRequest().build();
        }

        final Page<StockDTO> page = stockService.getAllTokensByExchange(exchange,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<StockDTO>> searchStock(@RequestParam(required = false) String exchange,@RequestParam String symbol,@ParameterObject Pageable pageable) {
        log.debug("REST request to search Stocks for given exchange {} and symbol {}" ,exchange, symbol);
        if (!onlyContainsAllowedProperties(pageable)) {
            return ResponseEntity.badRequest().build();
        }

        final Page<StockDTO> page = stockService.searchTokensByExchange(exchange,symbol,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    private boolean onlyContainsAllowedProperties(Pageable pageable) {
        return pageable.getSort().stream().map(Sort.Order::getProperty).allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
    }

}
