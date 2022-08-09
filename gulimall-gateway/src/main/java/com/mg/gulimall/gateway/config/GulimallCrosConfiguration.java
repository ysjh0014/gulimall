package com.mg.gulimall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GulimallCrosConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter(){

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration configurationSource = new CorsConfiguration();
        configurationSource.addAllowedHeader("*");
        configurationSource.addAllowedMethod("*");
        configurationSource.addAllowedOrigin("*");
        configurationSource.setAllowCredentials(true);

        source.registerCorsConfiguration("/**",configurationSource);

        return new CorsWebFilter(source);
    }
}
