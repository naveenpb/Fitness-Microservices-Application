package com.fitness.Gateway;

import com.fitness.Gateway.user.RegisterRequest;
import com.fitness.Gateway.user.UserValidationService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
// For every request, we synchronize users between Keycloak and the user microservice.
// Here, we check and potentially create a user in our backend whenever a request is triggered.
// This synchronization is handled at the API gateway level.
public class KeycloakUserSyncFilter implements WebFilter {

    private final UserValidationService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Mono represents a single async value.
        // ServerWebExchange wraps the HTTP request/response in reactive apps.
        // WebFilter is used to intercept and manipulate every web request.

        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID"); // Custom header for user identity
        String token = exchange.getRequest().getHeaders().getFirst("Authorization"); // Auth token

        RegisterRequest registerRequest = getUserDetails(token);

        if(userId == null){
            userId = registerRequest.getKeycloakId();
        }

        if (userId != null && token != null) {
            String finalUserId = userId;
            return userService.validateUser(userId)
                    .flatMap(exist -> {
                        if (!exist) {
                            // Register the user if needed

                            if(registerRequest!=null){
                                return userService.registerUser(registerRequest)
                                        .then(Mono.empty());
                            }
                            // You can return a user registration Mono here
                            return Mono.empty(); // placeholder
                        } else {
                            log.info("User already exists, skipping sync.");
                            return Mono.empty();
                        }
                    })
                    .then(Mono.defer(() -> {
                        // Mutate the request to include the user ID header
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header("X-User-ID", finalUserId)
                                .build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }));
        }

        // Continue without user sync if headers are missing
        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetails(String token) {

        try{
            String tokenWithoutBearer = token.replace("Bearer " , "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            RegisterRequest registerRequest =  new RegisterRequest();
            registerRequest.setEmail(claims.getStringClaim("email"));
            registerRequest.setKeycloakId(claims.getStringClaim("sub"));
            registerRequest.setPassword("dummy@123123");
            registerRequest.setFirstname(claims.getStringClaim("given_name"));
            registerRequest.setLastname(claims.getStringClaim("family_name"));
            return registerRequest;


        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
