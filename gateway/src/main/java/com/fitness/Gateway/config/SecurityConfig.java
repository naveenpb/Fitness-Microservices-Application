package com.fitness.Gateway.config;

import org.springframework.context.annotation.Bean; // Bean annotation allows Spring to manage this method's return value.
import org.springframework.context.annotation.Configuration; // Marks this class as a configuration class for Spring.
import org.springframework.security.config.Customizer; // Customizer helps with customizing the behavior of Spring Security.
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity; // Enable Spring Security for a WebFlux application (reactive programming model).
import org.springframework.security.config.web.server.ServerHttpSecurity; // This class is used to configure HTTP security for a WebFlux application.
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder; // Nimbus implementation for decoding JWTs using a JWK Set URI.
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder; // Interface for JWT decoding in a reactive application.
import org.springframework.security.web.server.SecurityWebFilterChain; // This is the WebFlux version of the SecurityFilterChain used in reactive applications.
import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;

@Configuration // Indicates that this class contains configuration settings for Spring (like beans).
@EnableWebFluxSecurity // Enables Spring Security's WebFlux support. This will add security to all routes handled by WebFlux (asynchronous).
public class SecurityConfig {

    // Define a method that configures the filter chain for security. Spring will call this method to set up the security filters.
    @Bean // Marks this method as a Spring Bean, so Spring will manage its lifecycle.
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disables CSRF protection for this application (common in stateless APIs).
                .authorizeExchange(exchange -> exchange
//                        .pathMatchers("/actuator/*").permitAll() // Uncomment to allow access to `/actuator/*` (e.g., health check) endpoints without authentication.
                                .anyExchange().authenticated() // Require authentication for all other requests (every route).
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) // Configures OAuth2 resource server with default JWT settings.
                .build(); // Finalizes and returns the configured security filter chain.
    }

    // Define a method that provides a custom JWT decoder to decode JWT tokens using a JWK set URI.
    @Bean // Marks this method as a Spring Bean.
    public ReactiveJwtDecoder reactiveJwtDecoder() {
//        its just a dummy configaration to supress errors
        return NimbusReactiveJwtDecoder.withJwkSetUri("http://localhost:8181/realms/fitness-oauth2/protocol/openid-connect/certs").build(); // Returns a NimbusReactiveJwtDecoder configured with the JWK Set URI.
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(Arrays.asList("GET" , "POST","PUT" ,"DELETE" ,"OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization" , "Content-Type" ,"X-User-ID"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**",config);
        return source;

    }
}
