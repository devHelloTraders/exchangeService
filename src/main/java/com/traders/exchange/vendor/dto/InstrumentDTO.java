package com.traders.exchange.vendor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class InstrumentDTO {
   @CsvBindByName(column = "SEM_SMST_SECURITY_ID")
    public long instrument_token;
   @CsvBindByName(column = "SEM_EXPIRY_CODE")
    public long exchange_token;
   @CsvBindByName(column = "SEM_TRADING_SYMBOL")
    public String tradingsymbol;
   @CsvBindByName(column = "SEM_INSTRUMENT_NAME")
    public String name;

    public double last_price;
   @CsvBindByName(column = "SEM_TICK_SIZE")
    public double tick_size;
   @CsvBindByName(column = "SEM_EXCH_INSTRUMENT_TYPE")
    public String instrument_type;
   @CsvBindByName(column = "SEM_SEGMENT")
    public String segment;
   @CsvBindByName(column = "SEM_EXM_EXCH_ID")
    public String exchange;
   @CsvBindByName(column = "SEM_STRIKE_PRICE")
    public String strike;
   @CsvBindByName(column = "SEM_LOT_UNITS")
    public float lot_size;
   @CsvBindByName(column = "SEM_EXPIRY_DATE")
   @CsvDate("yyyy-MM-dd HH:mm")
    public Date expiry;

}
