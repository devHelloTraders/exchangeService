package com.traders.exchange.vendor.contract;

import com.traders.common.appconfig.util.HeaderUtil;
import com.traders.common.model.MarketDetailsRequest;
import com.traders.common.model.MarketQuotes;
import com.traders.exchange.vendor.functions.GeneralFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")

public class ExchangeResource {


    private static final Logger LOG = LoggerFactory.getLogger(ExchangeResource.class);
    private final ExchangeClient exchangeClient;
    @Value("${config.clientApp.name}")
    private String applicationName;

    public ExchangeResource(ExchangeClient exchangeClient) {

        this.exchangeClient = exchangeClient;
    }

    @GetMapping("/admin/renewsession")
    public ResponseEntity<Void> renewSession() {
        LOG.debug("REST request to renew session");
        exchangeClient.renewClientSession();
        return new ResponseEntity<>(
                HttpStatus.OK);
    }


    @GetMapping("/admin/startwebsocket")
    public ResponseEntity<Void> startWebSocket() {
        LOG.debug("REST request to Start websocket session");
        exchangeClient.renewClientSession();
        return new ResponseEntity<>(
                HeaderUtil.createAlert(applicationName,"Websocket connection started",""),
                HttpStatus.OK);
    }

//    @GetMapping("/admin/stopwebsocket")
//    public ResponseEntity<Void> stopWebScoket() {
//        LOG.debug("REST request to stop websocket session");
//        dhanService.stopWebSocket();
//        return new ResponseEntity<>(
//                HeaderUtil.createAlert(applicationName,"Websocket connection started",""),
//                HttpStatus.OK);
//    }

    @Scheduled(cron = "0 00 9,16,20 * * *", zone = "Asia/Kolkata")
    @GetMapping("/admin/renewWebSocket")
    public ResponseEntity<Void> renewClientSession() {
        LOG.debug("request to Renew websocket session");
        exchangeClient.renewClientSession();
        return new ResponseEntity<>(
                HeaderUtil.createAlert(applicationName,"Websocket connection started",""),
                HttpStatus.OK);
    }


    @PostMapping("/machine/subscribe")
    public ResponseEntity<Void> subscribeInstruments(@RequestBody MarketDetailsRequest request) {
        LOG.debug("REST request to subscribe websocket session");
        exchangeClient.subscribe(request);
        return new ResponseEntity<>(
                HeaderUtil.createAlert(applicationName,"Websocket connection started",""),
                HttpStatus.OK);
    }


    @PostMapping("/machine/quotes")
    public ResponseEntity<Map<String, MarketQuotes>> getQuotesFromMarketList(@RequestBody MarketDetailsRequest request) {
        LOG.debug("REST request to get Market quotes via Rest");

        return new ResponseEntity<>(exchangeClient.getQuotes(GeneralFunctions.getSubscribeInstrumentInfos(request.getSubscribeInstrumentDetailsList())
        ), HeaderUtil.createAlert(applicationName,"Send quotes response",""), HttpStatus.OK);
    }


}
