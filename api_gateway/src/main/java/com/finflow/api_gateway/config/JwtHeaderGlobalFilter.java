package com.finflow.api_gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.Objects;

@Component
public class JwtHeaderGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtHeaderGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Skip JWT header propagation for Swagger/OpenAPI documentation paths
        if (path.contains("/v3/api-docs") || path.contains("/swagger-ui") || path.contains("/webjars")) {
            return chain.filter(exchange);
        }

        return exchange.getPrincipal()
                .filter(principal -> principal instanceof Authentication)
                .cast(Authentication.class)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof Jwt)
                .cast(Jwt.class)
                .map(jwt -> {
                    String userId = Objects.toString(jwt.getClaims().get("userId"), "");
                    String role = Objects.toString(jwt.getClaims().get("role"), "");
                    String email = jwt.getSubject();

                    log.debug("Populating headers - UserID: {}, Role: {}", userId, role);

                    ServerHttpRequest request = exchange.getRequest().mutate()
                            .header("X-User-Id", userId)
                            .header("X-User-Role", role)
                            .header("X-User-Email", email)
                            .build();
                    return exchange.mutate().request(request).build();
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        return -1; // run before other routing filters
    }
}
