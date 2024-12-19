package com.traders.exchange.service;

import com.google.common.base.Strings;
import com.traders.common.utils.EncryptionUtil;
import com.traders.exchange.config.AsyncConfiguration;
import com.traders.exchange.domain.InstrumentInfo;
import com.traders.exchange.exception.AttentionAlertException;
import com.traders.exchange.properties.ConfigProperties;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.Quote;
import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
public class KiteService {
    private final KiteConnect kiteConnect;
    private final StockService stockService;
    private final RedisService redisService;
    private final ConfigProperties configProperties;
    private final ModelMapper modelMapper;
    private final AsyncConfiguration asyncConfiguration;

    public KiteService(ConfigProperties configProperties,
                       StockService stockService,
                       RedisService redisService, ModelMapper modelMapper, AsyncConfiguration asyncConfiguration
    )  {
        kiteConnect = new KiteConnect(configProperties.getKiteConfig().getApiKey());
        this.stockService = stockService;
        this.redisService = redisService;
        this.configProperties = configProperties;
        this.modelMapper = modelMapper;
        this.asyncConfiguration = asyncConfiguration;
    }
    @SneakyThrows
    public void renewSession(String requestId,String uuid) {
        if(Strings.isNullOrEmpty(uuid) || Strings.isNullOrEmpty(requestId) || !uuid.equals(redisService.getSessionValue("uuid"))){
            throw new AttentionAlertException("Random UUID not matched", "KiteService","Can not create Kite session because UUID or Request id is not valid");
        }
        var kiteUser =kiteConnect.generateSession(requestId, configProperties.getKiteConfig().getApiSecret());
        kiteConnect.setAccessToken(kiteUser.accessToken);
        kiteConnect.setPublicToken(kiteUser.publicToken);
        redisService.saveToSessionCacheWithTTL("encryptedToken", EncryptionUtil.encrypt(kiteUser.accessToken),24,TimeUnit.HOURS);
        log.debug("Session renewed loading instruments");
        getAllInstruments();
    }

    @SneakyThrows
    public String getLoginUrl() {
        String randomNonce = UUID.randomUUID().toString();
        redisService.saveToSessionCache("uuid",randomNonce);
       return kiteConnect.getLoginURL()+"&redirect_params=uuid="+randomNonce;
    }
    @SneakyThrows
    private void loadForExchange(String exchangeName){

        var instruments =kiteConnect.getInstruments(exchangeName);
        stockService.upsertStocks(new ArrayList<>());
        log.info("upserted {} exchange {} record inserted",exchangeName, instruments.size());

    }
   // @Scheduled(cron = "0 55 19 * * *")
    public void getAllInstruments(){
        LocalDateTime lastRun = redisService.getSessionObjectValue("lastInstrumentLoaded");
        if(lastRun!=null && lastRun.isAfter(LocalDateTime.now()
                .minusHours(configProperties.getKiteConfig().getInstrumentLoadDelta()))){
            log.info("Instrument Synced at {} so skipping this time", lastRun);
            return;
        }

        Stream.of(configProperties.getKiteConfig().getSupportedExchange().split(","))
                        .map(String::trim)
                .forEach(this::loadForExchange);

        redisService.saveToSessionCacheWithTTL("lastInstrumentLoaded",LocalDateTime.now(),configProperties.getKiteConfig().getInstrumentLoadDelta(), TimeUnit.of(ChronoUnit.HOURS));
    }
    @Scheduled(cron = "0 59 22 * * *")
    @SneakyThrows
    /** Demonstrates com.zerodhatech.ticker connection, subcribing for instruments, unsubscribing for instruments, set mode of tick data, com.zerodhatech.ticker disconnection*/
    public void tickerUsage() {
        /** To get live price use websocket connection.
         * It is recommended to use only one websocket connection at any point of time and make sure you stop connection, once user goes out of app.
         * custom url points to new endpoint which can be used till complete Kite Connect 3 migration is done. */
        kiteConnect.setAccessToken(EncryptionUtil.decrypt(redisService.getSessionValue("encryptedToken")));
        final KiteTicker tickerProvider = new KiteTicker(kiteConnect.getAccessToken(), kiteConnect.getApiKey());
       // ArrayList<Long> tokenList = stockService.getAllTokens();
        //need to get only watchlist portfolio stock and on page live stock
        ArrayList<Long> tokenList = new ArrayList<>(stockService.getAllTokens().stream().map(InstrumentInfo::getInstrumentToken).limit(3000).toList());
        tickerProvider.setOnConnectedListener(new OnConnect() {
            @Override
            public void onConnected() {
                /** Subscribe ticks for token.
                 * By default, all tokens are subscribed for modeQuote.
                 * */
                tickerProvider.subscribe(tokenList);
                tickerProvider.setMode(tokenList, KiteTicker.modeFull);
            }
        });

        tickerProvider.setOnDisconnectedListener(new OnDisconnect() {
            @Override
            public void onDisconnected() {
                // your code goes here
            }
        });

        /** Set listener to get order updates.*/
        tickerProvider.setOnOrderUpdateListener(new OnOrderUpdate() {
            @Override
            public void onOrderUpdate(Order order) {
                System.out.println("order update "+order.orderId);
            }
        });

        /** Set error listener to listen to errors.*/
        tickerProvider.setOnErrorListener(new OnError() {
            @Override
            public void onError(Exception exception) {
                System.out.println("Error occurred ");
            }

            @Override
            public void onError(KiteException kiteException) {
                System.out.println("Error occurred ");
            }

            @Override
            public void onError(String error) {
                System.out.println(error);
            }
        });

        tickerProvider.setOnTickerArrivalListener(new OnTicks() {
            @Override
            public void onTicks(ArrayList<Tick> ticks) {

                if(ticks.isEmpty())
                    return;
                ticks.parallelStream().forEach(tick->{
                    com.traders.exchange.vendor.dto.Tick ticksDto = new com.traders.exchange.vendor.dto.Tick();
                    modelMapper.map(tick,ticksDto);
                    log.debug("Received Price Update for tick {}", tick.getInstrumentToken());
                    redisService.addStockCache(String.valueOf(tick.getInstrumentToken()),ticksDto);
                });
//                NumberFormat formatter = new DecimalFormat();
//                System.out.println("ticks size "+ticks.size());
//                if(ticks.size() > 0) {
//                    System.out.println("last price "+ticks.get(0).getLastTradedPrice());
//                    System.out.println("open interest "+formatter.format(ticks.get(0).getOi()));
//                    System.out.println("day high OI "+formatter.format(ticks.get(0).getOpenInterestDayHigh()));
//                    System.out.println("day low OI "+formatter.format(ticks.get(0).getOpenInterestDayLow()));
//                    System.out.println("change "+formatter.format(ticks.get(0).getChange()));
//                    System.out.println("tick timestamp "+ticks.get(0).getTickTimestamp());
//                    System.out.println("tick timestamp date "+ticks.get(0).getTickTimestamp());
//                    System.out.println("last traded time "+ticks.get(0).getLastTradedTime());
//                    System.out.println(ticks.get(0).getMarketDepth().get("buy").size());
//                }
            }
        });
        // Make sure this is called before calling connect.
        tickerProvider.setTryReconnection(true);
        //maximum retries and should be greater than 0
        tickerProvider.setMaximumRetries(10);
        //set maximum retry interval in seconds
        tickerProvider.setMaximumRetryInterval(30);

        /** connects to com.zerodhatech.com.zerodhatech.ticker server for getting live quotes*/
        tickerProvider.connect();

        /** You can check, if websocket connection is open or not using the following method.*/
        boolean isConnected = tickerProvider.isConnectionOpen();
        System.out.println(isConnected);

        /** set mode is used to set mode in which you need tick for list of tokens.
         * Ticker allows three modes, modeFull, modeQuote, modeLTP.
         * For getting only last traded price, use modeLTP
         * For getting last traded price, last traded quantity, average price, volume traded today, total sell quantity and total buy quantity, open, high, low, close, change, use modeQuote
         * For getting all data with depth, use modeFull*/
        tickerProvider.setMode(tokenList, KiteTicker.modeLTP);

        // Unsubscribe for a token.
        //tickerProvider.unsubscribe(tokenList);

        // After using com.zerodhatech.com.zerodhatech.ticker, close websocket connection.
        //tickerProvider.disconnect();
    }


    public void getQuote(String[] instrument) throws KiteException, IOException {
        String[] instruments = {"256265","BSE:INFY", "NSE:APOLLOTYRE", "NSE:NIFTY 50", "24507906"};
        Map<String, Quote> quotes = kiteConnect.getQuote(instruments);
        System.out.println(quotes.get("NSE:APOLLOTYRE").instrumentToken+"");
        System.out.println(quotes.get("NSE:APOLLOTYRE").oi +"");
        System.out.println(quotes.get("NSE:APOLLOTYRE").depth.buy.get(4).getPrice());
        System.out.println(quotes.get("NSE:APOLLOTYRE").timestamp);
        System.out.println(quotes.get("NSE:APOLLOTYRE").lowerCircuitLimit+"");
        System.out.println(quotes.get("NSE:APOLLOTYRE").upperCircuitLimit+"");
        System.out.println(quotes.get("24507906").oiDayHigh);
        System.out.println(quotes.get("24507906").oiDayLow);
    }

    public void updateStocks(){

    }

   private void warmupConnections(){

   }
}
