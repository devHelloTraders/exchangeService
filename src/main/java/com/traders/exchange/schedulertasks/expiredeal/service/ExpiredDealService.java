package com.traders.exchange.schedulertasks.expiredeal.service;

import com.traders.exchange.schedulertasks.expiredeal.model.ExpiredDeal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ExpiredDealService {
    private final NamedParameterJdbcTemplate template;
    private final Logger logger = LoggerFactory.getLogger(ExpiredDealService.class);

    public ExpiredDealService(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    public List<ExpiredDeal> getExpiredDeals(String expiryDate, List<String> exchanges) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT psd.id AS id,psd.quantity AS quantity,psd.stock_id AS stockId,last_price AS lastPrice ")
                .append("FROM portfolio_stocks_detail psd ")
                .append("JOIN stock_details sd ")
                .append("ON psd.stock_id = sd.id ")
                .append("WHERE sd.expiry = :expiryDate ")
                .append("AND sd.exchange IN (:exchanges) ")
                .append("AND psd.deleteflag = 0 AND quantity > 0");
        return template.query(query.toString(),
                new MapSqlParameterSource("expiryDate", expiryDate)
                        .addValue("exchanges", exchanges), new BeanPropertyRowMapper<>(ExpiredDeal.class));
    }
}
