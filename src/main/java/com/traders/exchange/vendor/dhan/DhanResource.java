package com.traders.exchange.vendor.dhan;

import com.traders.common.appconfig.util.HeaderUtil;
import com.traders.common.constants.AuthoritiesConstants;
import com.traders.exchange.vendor.kite.KiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
@ConditionalOnProperty(
        name = "config.dhanConfig.active",
        havingValue = "true",
        matchIfMissing = true
)
public class DhanResource {


    private static final Logger LOG = LoggerFactory.getLogger(DhanResource.class);
    private final DhanClient dhanClient;
    private final DhanService dhanService;
    @Value("${config.clientApp.name}")
    private String applicationName;

    public DhanResource(DhanClient dhanClient, DhanService dhanService) {

        this.dhanClient = dhanClient;
        this.dhanService = dhanService;
    }

    @GetMapping("/admin/renewsession")
    public ResponseEntity<Void> renewSession(@RequestParam String request_token,@RequestParam String uuid) {
        LOG.debug("REST request to renew session");
        dhanClient.renewSession(request_token,uuid);
        return new ResponseEntity<>(
                HttpStatus.OK);
    }

    @GetMapping("/getlogin")
    public ResponseEntity<String> getLoginUrl() {
        LOG.debug("REST request to renew session");
        return new ResponseEntity<>(dhanClient.getLoginUrl(),
                HeaderUtil.createAlert(applicationName,"Kite login url generated",""),
                HttpStatus.OK);
    }
    @GetMapping("/admin/startwebsocket")
    public ResponseEntity<Void> startWebScoket() {
        LOG.debug("REST request to Start websocket session");
        dhanClient.getInstrumentsToSubScribe();
        return new ResponseEntity<>(
                HeaderUtil.createAlert(applicationName,"Websocket connection started",""),
                HttpStatus.OK);
    }

    @GetMapping("/admin/stopwebsocket")
    public ResponseEntity<Void> stopWebScoket() {
        LOG.debug("REST request to stop websocket session");
        dhanService.stopWebSocket();
        return new ResponseEntity<>(
                HeaderUtil.createAlert(applicationName,"Websocket connection started",""),
                HttpStatus.OK);
    }



}