package com.finflow.api_gateway;

import com.finflow.api_gateway.service.TokenRevocationService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false",
                "management.tracing.enabled=false"
        }
)
@AutoConfigureWebTestClient
class AuthLogoutControllerTest {

    private static final String SECRET = "finflowsecretkeyforhs256algorithm";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TokenRevocationService tokenRevocationService;

    @Test
    void logoutRequiresBearerToken() {
        webTestClient.post()
                .uri("/auth/logout")
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void logoutRevokesTokenAndFutureRequestsAreRejected() {
        String token = createToken(Duration.ofHours(1));

        webTestClient.post()
                .uri("/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Logged out successfully");

        assertThat(tokenRevocationService.isRevoked(token)).isTrue();

        webTestClient.get()
                .uri("/applications/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    private String createToken(Duration lifetime) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + lifetime.toMillis());

        return Jwts.builder()
                .setSubject("user@example.com")
                .claim("userId", 1L)
                .claim("role", "APPLICANT")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
}
