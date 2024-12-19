package com.traders.exchange.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration extends com.traders.common.config.JacksonConfiguration {

//
//    @Bean
//    public JavaTimeModule javaTimeModule() {
//        return new JavaTimeModule();
//    }
//
//    @Bean
//    public Jdk8Module jdk8TimeModule() {
//        return new Jdk8Module();
//    }
//
//    /*
//     * Support for Hibernate types in Jackson.
//     */
//    @Bean
//    public Hibernate6Module hibernate6Module() {
//        return new Hibernate6Module().configure(Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);
//    }
}
