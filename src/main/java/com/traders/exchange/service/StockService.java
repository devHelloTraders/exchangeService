package com.traders.exchange.service;

import com.traders.common.model.InstrumentInfo;
import com.traders.common.model.MarketDetailsRequest;
import com.traders.common.model.MarketQuotes;
import com.traders.exchange.config.AsyncConfiguration;
import com.traders.exchange.config.HikariConfiguration;
import com.traders.exchange.domain.Stock;
import com.traders.exchange.exception.AttentionAlertException;
import com.traders.exchange.exception.BadRequestAlertException;
import com.traders.exchange.properties.ConfigProperties;
import com.traders.exchange.repository.StockRepository;
import com.traders.exchange.service.dto.StockDTO;
import com.traders.exchange.service.dto.UnsubscribeInstrument;
import com.traders.exchange.vendor.contract.ExchangeClient;
import com.traders.exchange.vendor.dhan.DhanExchangeResolver;
import com.traders.exchange.vendor.dhan.ExchangeSegment;
import com.traders.exchange.vendor.dto.InstrumentDTO;
import com.traders.exchange.vendor.functions.GeneralFunctions;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

@Service
@Slf4j
public class StockService {

    private final StockRepository stockRepository;
    //private final MapperS
    private final Converter<InstrumentDTO, Stock> instrumentStockConverter;
    private final ConfigProperties configProperties;
    private final AsyncConfiguration asyncConfiguration;
    private final HikariConfiguration hikariConfiguration;
    private final RedisService redisService;
    private final ModelMapper mapper;
    private final ExchangeClient exchangeClient;
    public StockService(ConfigProperties configProperties, StockRepository stockRepository, ModelMapper modelMapper, AsyncConfiguration asyncConfiguration, HikariConfiguration hikariConfiguration, RedisService redisService, ExchangeClient exchangeClient) {
        this.stockRepository = stockRepository;
        this.configProperties =configProperties;
        this.instrumentStockConverter   = instrument -> {
            Stock stock = new Stock();
            modelMapper.map(instrument, stock);
            stock.setInstrumentToken(instrument.instrument_token);
            stock.setExchangeToken(instrument.exchange_token);
            stock.setLotSize(instrument.lot_size);
            stock.setLastPrice(instrument.last_price);
            stock.setTickSize(instrument.tick_size);
            stock.setInstrumentType(instrument.instrument_type);
            return stock;
        };
        this.asyncConfiguration = asyncConfiguration;
        this.hikariConfiguration = hikariConfiguration;
        this.redisService = redisService;
        this.mapper = modelMapper;
        this.exchangeClient = exchangeClient;
    }
    static int temp =0;

    @Transactional
    public void upsertStocks(List<InstrumentDTO> instruments){
        if(instruments ==null || instruments.isEmpty()){
            throw new AttentionAlertException("Instruments is empty", "Stock Service","Instrument is not loaded correctly please have a look.");
        }

        asyncConfiguration.scaleUpExecutors();
        hikariConfiguration.increaseConnectionPool();
        var stockList =instruments.stream()
                .filter(Objects::nonNull)
                .filter(instrument -> instrument.getName() !=null)
                .map(instrumentDTO -> {
                    instrumentDTO.setExchange(DhanExchangeResolver.getCategory(instrumentDTO));
                    return instrumentDTO;
                }).filter(instrumentDTO -> !instrumentDTO.getExchange().equalsIgnoreCase("UNKNOWN_CATEGORY"))
                .map(instrumentStockConverter::convert)
                .toList();
        final int batchSize = configProperties.getStockConfig().getBatchSize();


        List<CompletableFuture<Void>> futures = IntStream.range(0, (stockList.size() + batchSize - 1) / batchSize)  // Generate batch indices
                .mapToObj(i -> stockList.subList(i * batchSize, Math.min((i + 1) * batchSize, stockList.size())))  // Create sublist for each batch
                .map(batch ->  CompletableFuture.runAsync(() -> {
                    // Process each stock asynchronously
                    saveStocks(batch);
                    saveToRedis(batch);
                }, asyncConfiguration.getAsyncExecutor())).toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(()->{
            asyncConfiguration.scaleDownExecutors();
            hikariConfiguration.reduceConnectionPool();
            log.debug("Scaling down connections");
        }) ;

        // saveStocks(stockList);
    }

    private void saveToRedis(List<Stock> stocks){
        stocks.forEach(stock-> redisService.saveToCache("stockNameCache",String.valueOf(stock.getInstrumentToken()),stock.getTradingSymbol()));

    }
    public void saveStocks(List<Stock> stocks){
        System.out.println("Saving stocks "+ ++temp);
        if(stocks ==null || stocks.isEmpty()){
            throw new BadRequestAlertException("Instruments is empty", "Stock Service","Instrument is not loaded correctly please have a look.");
        }
        log.info("Saving stocks into repository with {} size",stocks.size());

        stockRepository.saveAll(stocks);
    }
//    @Transactional
//    public List<InstrumentInfo> getAllTokens(){
//        Date now = new Date();
//        LocalDateTime localDateTime = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//        LocalDateTime updatedDateTime = localDateTime.plusDays(configProperties.getDhanConfig().getAllowedDaysRange());
//        return stockRepository.findByIsActiveTrueAndExpiryIsNullOrExpiryBetween(now,Date.from(updatedDateTime.atZone(ZoneId.systemDefault()).toInstant()));
//    }

    @Transactional
    public List<InstrumentInfo> getAllTokens(){
        return stockRepository.findAllUniqueWatchStocks();
    }


    @Transactional
    public Page<Stock> getAllTokensByExchange(ExchangeSegment exchange, Pageable pageable){
        Date now = new Date();
        LocalDateTime localDateTime = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updatedDateTime = localDateTime.plusDays(configProperties.getDhanConfig().getAllowedDaysRange());
        return stockRepository.findByIsActiveTrueAndExchangeAndExpiryIsNullOrExpiryBetween(exchange.name(),now,
                Date.from(updatedDateTime.atZone(ZoneId.systemDefault()).toInstant()),exchange.getInstrumentTypes(),pageable);
    }

    @Transactional
    public Page<Stock> searchTokensByExchange(ExchangeSegment exchange,String searchName,Pageable pageable){
        Date now = new Date();
        LocalDateTime localDateTime = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updatedDateTime = localDateTime.plusDays(configProperties.getDhanConfig().getAllowedDaysRange());
       if (exchange==null)
            return stockRepository.findByIsActiveTrueAndSymbolAndExpiryIsNullOrExpiryBetween(searchName,now,
                Date.from(updatedDateTime.atZone(ZoneId.systemDefault()).toInstant()),pageable);

       return stockRepository.findByIsActiveTrueAndExchangeAndSymbolAnsExpiryIsNullOrExpiryBetween(searchName,exchange.name(),now,
                Date.from(updatedDateTime.atZone(ZoneId.systemDefault()).toInstant()),exchange.getInstrumentTypes(),pageable);
    }

    public List<StockDTO> getStockDtoList(List<Stock> stockList, List<UnsubscribeInstrument> unsubscribeInstruments){
        MarketDetailsRequest request =new MarketDetailsRequest();
        stockList.forEach(stock-> request.addInstrument(MarketDetailsRequest.InstrumentDetails.of(stock.getInstrumentToken(),stock.getExchange(),stock.getName())));
        if(unsubscribeInstruments !=null)
            unsubscribeInstruments.forEach(unsub-> request.removeInstrument(MarketDetailsRequest.InstrumentDetails.of(unsub.getInstrumentId(),unsub.getExchange(),"")));

        var marketResponse = getQuotesFromMarketList(GeneralFunctions.getSubscribeInstrumentInfos(request.getSubscribeInstrumentDetailsList()));
        exchangeClient.subscribeInstrument(request);
        return stockList.stream().map(stock->{
            StockDTO stockDTO = new StockDTO();
            mapper.map(stock,stockDTO);
            MarketQuotes quotes = marketResponse.get(String.valueOf(stock.getInstrumentToken()));
            stockDTO.setQuotes(quotes);
            stockDTO.updatePrice();
            return stockDTO;
        }).toList();

    }

    public Map<String,MarketQuotes> getQuotesFromMarketList(List<InstrumentInfo> instrumentInfos){
        var marketQuotes = exchangeClient.getAllMarketQuoteViaRest(instrumentInfos);
        if(marketQuotes == null || marketQuotes.isEmpty()){
            throw new BadRequestAlertException("Invalid Stock Request","StockService","Please pass Correct stock id");
        }
        return marketQuotes;
    }

    public List<StockDTO> mapQuotesToDTO(List<StockDTO> stockList){
        MarketDetailsRequest request =new MarketDetailsRequest();
        stockList.forEach(stock-> request.addInstrument(MarketDetailsRequest.InstrumentDetails.of(stock.getInstrumentToken(),stock.getExchange(),stock.getTradingSymbol())));
        exchangeClient.subscribeInstrument(request);
        var marketResponse = getQuotesFromMarketList(GeneralFunctions.getSubscribeInstrumentInfos(request.getSubscribeInstrumentDetailsList()));
        stockList.forEach(stockDTO->{
            MarketQuotes quotes = marketResponse.get(String.valueOf(stockDTO.getInstrumentToken()));
            stockDTO.setQuotes(quotes);
            stockDTO.updatePrice();
        });
        return stockList;
    }
}
