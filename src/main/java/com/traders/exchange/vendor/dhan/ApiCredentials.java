package com.traders.exchange.vendor.dhan;

import com.traders.common.utils.EncryptionUtil;
import com.traders.common.utils.JWTUtils;
import com.traders.exchange.exception.AttentionAlertException;
import com.traders.exchange.properties.ConfigProperties;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

@Component
public class ApiCredentials {

    private final ConfigProperties configProperties;

    public ApiCredentials(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    private List<Credentials> apiCredential = new ArrayList<>();
    @Getter
    public static class Credentials{
        private final String apiKey;
        private final String clientId;

        public Credentials(Pair<String,String> credentialData){
            this.clientId = credentialData.getKey();
            this.apiKey= credentialData.getValue();
        }
    }


    @SneakyThrows
    public List<Credentials> getCredentials(){
        if(apiCredential.isEmpty())
            apiCredential=loadCredentials();
        return apiCredential;
    }

    private List<Credentials> loadCredentials(){
        List<String> configuredCredentials = configProperties.getDhanConfig().getApiCredentials();
        return configuredCredentials.stream()
                .map(EncryptionUtil::decrypt)
                .map(stringOptionalFunction)
                .filter(credential ->JWTUtils.isTokenValid(credential.getValue()))
                .map(Credentials::new)
                .toList();
    }

    private final Function<String, Pair<String,String>> stringOptionalFunction = token -> {
        if (token == null || !token.contains(":")) {
            throw new AttentionAlertException("Dhan Credentials are not in valid form","ApiCredentails","Please contact Admin");
        }
        String[] parts = token.split(":", 2);
        return Pair.of(parts[0].trim(), parts[1].trim());
    };

    public Credentials getRandomConnection(){
        int credentialSize = getCredentials().size();
        int randomNumber =credentialSize==1 ? 0 : new Random().nextInt(credentialSize);
        return getCredentials().get(randomNumber);
    }
}
