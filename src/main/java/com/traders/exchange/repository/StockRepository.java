package com.traders.exchange.repository;

import com.traders.exchange.domain.Stock;
import com.traders.exchange.domain.InstrumentInfo;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock,Long> {

    List<Stock> findAllByIdIn (List<Long> ids);
    List<InstrumentInfo> findByIsActiveTrueAndExpiryAfter(Date now);
    List<InstrumentInfo> findByIsActiveTrueAndExpiryAfterAndExchange(Date now,String exchange);
    @Query("SELECT i FROM Stock i WHERE i.isActive = true AND i.exchange = :exchange AND (i.expiry IS NULL OR i.expiry BETWEEN :now AND :futureDate)")
    Page<Stock> findByIsActiveTrueAndExchangeAndExpiryIsNullOrExpiryBetween(@Param("exchange") String exchange,
                                                                            @Param("now") Date now,
                                                                            @Param("futureDate") Date futureDate,
                                                                            Pageable pageable);

    @Query("SELECT i FROM Stock i WHERE i.isActive = true AND i.exchange = :exchange AND i.tradingSymbol like %:symbol% AND (i.expiry IS NULL OR i.expiry BETWEEN :now AND :futureDate)")
    Page<Stock> findByIsActiveTrueAndExchangeAndSymbolAnsExpiryIsNullOrExpiryBetween(
                                                                            @Param("symbol") String symbol,
                                                                            @Param("exchange") String exchange,
                                                                            @Param("now") Date now,
                                                                            @Param("futureDate") Date futureDate,
                                                                            Pageable pageable);


    @Query("SELECT i FROM Stock i WHERE i.isActive = true AND i.tradingSymbol like %:symbol% AND (i.expiry IS NULL OR i.expiry BETWEEN :now AND :futureDate)")
    Page<Stock> findByIsActiveTrueAndSymbolAnsExpiryIsNullOrExpiryBetween(
                                                                         @Param("symbol") String symbol,
                                                                         @Param("now") Date now,
                                                                         @Param("futureDate") Date futureDate,
                                                                         Pageable pageable);

    @Query("SELECT i FROM Stock i WHERE i.isActive = true AND (i.expiry IS NULL OR i.expiry BETWEEN :now AND :futureDate)")

    List<InstrumentInfo> findByIsActiveTrueAndExpiryIsNullOrExpiryBetween(@Param("now") Date now, @Param("futureDate") Date futureDate);


}
