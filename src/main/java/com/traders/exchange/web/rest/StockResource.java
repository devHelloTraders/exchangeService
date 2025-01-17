package com.traders.exchange.web.rest;

import com.traders.common.appconfig.util.PaginationUtil;
import com.traders.exchange.domain.Stock;
import com.traders.exchange.service.StockService;
import com.traders.exchange.service.dto.StockDTO;
import com.traders.exchange.service.dto.UnsubscribeInstrument;
import com.traders.exchange.vendor.dhan.ExchangeSegment;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
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
    public ResponseEntity<List<StockDTO>> getAllStocks(@RequestParam @NotNull ExchangeSegment exchange,@RequestBody List<UnsubscribeInstrument> instrument, @ParameterObject Pageable pageable) {
        log.debug("REST request to get all Stocks for given exchange");
        if (!PaginationUtil.onlyContainsAllowedProperties(pageable,ALLOWED_ORDERED_PROPERTIES)) {
            return ResponseEntity.badRequest().build();
        }

        final Page<Stock> page = stockService.getAllTokensByExchange(exchange,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(mapStockToDTO(page.getContent(),instrument), headers, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<StockDTO>> searchStock(@RequestParam(required = false) ExchangeSegment exchange,@RequestParam String symbol,@RequestBody List<UnsubscribeInstrument> instruments,@ParameterObject Pageable pageable) {
        log.debug("REST request to search Stocks for given exchange {} and symbol {}" ,exchange, symbol);
        if (!PaginationUtil.onlyContainsAllowedProperties(pageable,ALLOWED_ORDERED_PROPERTIES)) {
            return ResponseEntity.badRequest().build();
        }

        final Page<Stock> page = stockService.searchTokensByExchange(exchange,symbol,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(mapStockToDTO(page.getContent(),instruments), headers, HttpStatus.OK);
    }


    private List<StockDTO> mapStockToDTO(List<Stock> stockList, List<UnsubscribeInstrument> unsubscribeInstrumentList){
        if(stockList==null || stockList.isEmpty()){
            return new ArrayList<>();
        }
        return stockService.getStockDtoList(stockList,unsubscribeInstrumentList);
    }
    @PostMapping("/machine/map")
    public List<StockDTO> getStocksViaRest(@RequestBody List<StockDTO> stockList){
        if(stockList==null || stockList.isEmpty()){
            return new ArrayList<>();
        }
        return stockService.mapQuotesToDTO(stockList);
    }


}
