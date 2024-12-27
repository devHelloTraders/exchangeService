package com.traders.exchange.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static String getTodayDate(){
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return zonedDateTime.format(dateTimeFormatter);
    }
}
