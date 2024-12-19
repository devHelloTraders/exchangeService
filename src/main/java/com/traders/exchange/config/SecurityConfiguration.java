package com.traders.exchange.config;

import com.traders.common.properties.ConfigProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends com.traders.common.config.SecurityConfiguration {
    public SecurityConfiguration(ConfigProperties configProperties, AuthenticationConfiguration authenticationConfiguration) {
        super(configProperties, authenticationConfiguration);
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
//        http.csrf((csrf) -> {
//            csrf.disable();
//        }).cors((httpSecurityCorsConfigurer) -> {
//            httpSecurityCorsConfigurer.configurationSource(this.corsConfigurationSource());
//        }).addFilterAfter(new SpaWebFilter(), BasicAuthenticationFilter.class).addFilterBefore(new DecryptUserIdFilter(), UsernamePasswordAuthenticationFilter.class).headers((headers) -> {
//            headers.contentSecurityPolicy((csp) -> {
//                csp.policyDirectives(this.configProperties.getSecurity().getContentSecurityPolicy());
//            }).frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin).referrerPolicy((referrer) -> {
//                referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN);
//            }).permissionsPolicy((permissions) -> {
//                permissions.policy("camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()");
//            });
//        }).authorizeHttpRequests((authz) -> {
//            ((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)authz.requestMatchers(new RequestMatcher[]{mvc.pattern("/app/**")})).permitAll().requestMatchers(new RequestMatcher[]{mvc.pattern("/i18n/**")})).permitAll().requestMatchers(new RequestMatcher[]{mvc.pattern("/index.html"), mvc.pattern("/*.js"), mvc.pattern("/*.txt"), mvc.pattern("/*.json"), mvc.pattern("/*.map"), mvc.pattern("/*.css")})).permitAll().requestMatchers(new RequestMatcher[]{mvc.pattern("/*.ico"), mvc.pattern("/*.png"), mvc.pattern("/*.svg"), mvc.pattern("/*.webapp")})).permitAll()
//                    .requestMatchers(new RequestMatcher[]{mvc.pattern("/v2/api-docs")})).permitAll()
//                    .requestMatchers(new RequestMatcher[]{mvc.pattern("/swagger-ui/**")})).permitAll()
//                    .requestMatchers(new RequestMatcher[]{mvc.pattern(HttpMethod.POST, "/api/authenticate")})).permitAll()
//                    .requestMatchers(new RequestMatcher[]{mvc.pattern(HttpMethod.GET, "/api/authenticate")})).permitAll()
//                    .requestMatchers(new RequestMatcher[]{mvc.pattern("/api/admin/**")})).hasAuthority("ROLE_ADMIN")
//                    .requestMatchers(new String[]{"/v3/api-docs/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"})).permitAll()
//                    .requestMatchers(new RequestMatcher[]{mvc.pattern("/management/health")})).permitAll()
//                    .requestMatchers(new RequestMatcher[]{mvc.pattern("/management/health/**")})).permitAll()
//                    .requestMatchers(new RequestMatcher[]{mvc.pattern("/management/info")})).permitAll()
//                    .requestMatchers(new RequestMatcher[]{mvc.pattern("/management/prometheus")})).permitAll()
//                    .requestMatchers(new RequestMatcher[]{mvc.pattern("/management/**")})).hasAuthority("ROLE_ADMIN")
//                    .requestMatchers(new RequestMatcher[]{mvc.pattern("/api/register")})).permitAll()
//                    .requestMatchers(new RequestMatcher[]{mvc.pattern("/api/activate")})).permitAll()
//                    .requestMatchers(new RequestMatcher[]{mvc.pattern("/api/account/reset-password/init")}))
//                    .permitAll().requestMatchers(new RequestMatcher[]{mvc.pattern("/api/account/reset-password/finish")}))
//                    .permitAll().requestMatchers(new RequestMatcher[]{mvc.pattern("/api/**")})).permitAll();
//        }).sessionManagement((session) -> {
//            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        }).exceptionHandling((exceptions) -> {
//            exceptions.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()).accessDeniedHandler(new BearerTokenAccessDeniedHandler());
//        }).oauth2ResourceServer((oauth2) -> {
//            oauth2.jwt(Customizer.withDefaults());
//        });
//        return (SecurityFilterChain)http.build();
//    }
//
////    @Bean
////    public AuthenticationManager authenticationManager(List<AuthenticationProvider> authenticationProviders) {
////        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
////       // authProvider.setUserDetailsService();
////        authProvider.setPasswordEncoder(passwordEncoder());
////        return new ProviderManager(authProvider);
////    }

}
