package com.traders.exchange.schedulertasks.expiredeal;

import com.traders.exchange.domain.OrderCategory;
import com.traders.exchange.domain.OrderType;
import com.traders.exchange.orders.TradeRequest;
import com.traders.exchange.orders.service.TradeFeignService;
import com.traders.exchange.schedulertasks.expiredeal.model.ExpiredDeal;
import com.traders.exchange.schedulertasks.expiredeal.service.ExpiredDealService;
import com.traders.exchange.utils.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DealExpiryScheduler {

    private final Logger logger = LoggerFactory.getLogger(DealExpiryScheduler.class);
    private final ExpiredDealService expiredDealService;
    private final TradeFeignService tradeService;

    public DealExpiryScheduler(ExpiredDealService expiredDealService, TradeFeignService tradeService) {
        this.expiredDealService = expiredDealService;
        this.tradeService = tradeService;
    }

    /**
     * This method is responsible for closing all
     * BSE and NSE instruments deal which are about to
     * expire today.
     * <p>
     * This will run every day at 3:30 PM IST.
     * </p>
     */
    @Scheduled(cron = "0 35 15 * * ?", zone = "Asia/Kolkata")
    public void closeBSEAndNSEDeals() {
        try{
            logger.info("Scheduler task started to close BSE and NSE segments deal.");
            String todaysDate= DateTimeUtil.getTodayDate();
            List<ExpiredDeal> expiredDeals=expiredDealService.getExpiredDeals(todaysDate, Arrays.asList("NSE_FNO","BSE_FNO"));
            int dealCloseCount = closeDeals(expiredDeals);
            logger.info("Scheduler task ended to close BSE and NSE segments deal.");
            logger.info("Total deal closes: "+dealCloseCount);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    /**
     * This method is responsible for closing all
     * open MCX instruments deal which are about to
     * expire today.
     * <p>
     * This will run every day at 11:30 PM IST.
     * </p>
     */
    @Scheduled(cron = "0 30 23 * * ?", zone = "Asia/Kolkata")
    public void closeMCXDeals() {
        try{
            logger.info("Scheduler task started to close MCX segments deal.");
            String todaysDate= DateTimeUtil.getTodayDate();
            List<ExpiredDeal> expiredDeals=expiredDealService.getExpiredDeals(todaysDate, Arrays.asList("MCX_COMM"));
            int dealCloseCount = closeDeals(expiredDeals);
            logger.info("Scheduler task ended to close MCX segments deal.");
            logger.info("Total deal closes: "+dealCloseCount);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    private TradeRequest getTradeRequest(ExpiredDeal expiredDeal){
        OrderType orderType=(expiredDeal.getQuantity()>0)?OrderType.SELL:OrderType.BUY;
        return new TradeRequest(
                expiredDeal.getQuantity(),
                orderType,
                OrderCategory.MARKET,
                expiredDeal.getStockId(),
                expiredDeal.getLastPrice(),null,null
        );
    }

    private int closeDeals(List<ExpiredDeal> expiredDeals) {
        int dealCloseCount=0;
        for(ExpiredDeal expiredDeal:expiredDeals){
            try{
                tradeService.addTradeTransaction(getTradeRequest(expiredDeal));
                dealCloseCount++;
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }
        return dealCloseCount;
    }
}
