package com.traders.exchange.vendor.kite;

import com.traders.common.appconfig.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        name = "config.kiteConfig.active",
        havingValue = "true"
)
public class KiteResource {


    private static final Logger LOG = LoggerFactory.getLogger(KiteResource.class);
    private final KiteService kiteService;
    @Value("${config.clientApp.name}")
    private String applicationName;

    public KiteResource(KiteService kiteService) {

        this.kiteService = kiteService;
    }

    @GetMapping("/renewsession")
    public ResponseEntity<Void> renewSession(@RequestParam String request_token,@RequestParam String uuid) {
        LOG.debug("REST request to renew session");
        kiteService.renewSession(request_token,uuid);
        return new ResponseEntity<>(
                HttpStatus.OK);
    }

    @GetMapping("/getlogin")
    public ResponseEntity<String> getLoginUrl() {
        LOG.debug("REST request to renew session");
        return new ResponseEntity<>(kiteService.getLoginUrl(),
                HeaderUtil.createAlert(applicationName,"Kite login url generated",""),
                HttpStatus.OK);
    }

}
