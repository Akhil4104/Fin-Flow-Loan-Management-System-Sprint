package com.finflow.api_gateway.config;

import com.finflow.api_gateway.service.TokenRevocationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtDecoderConfig {

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(
            @Value("${jwt.secret}") String secret,
            TokenRevocationService tokenRevocationService) {
        var secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        ReactiveJwtDecoder delegate = NimbusReactiveJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        return token -> delegate.decode(token)
                .flatMap(jwt -> tokenRevocationService.isRevoked(token)
                        ? Mono.error(new BadJwtException("Token has been revoked"))
                        : Mono.just(jwt));
    }
}
