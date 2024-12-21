package com.traders.exchange.vendor.dhan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traders.common.model.MarketQuotes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketQuotesResponse implements Serializable {
    private String status;

    @JsonProperty("data")
    private Map<String, Map<String, MarketQuotes>> data;
}
