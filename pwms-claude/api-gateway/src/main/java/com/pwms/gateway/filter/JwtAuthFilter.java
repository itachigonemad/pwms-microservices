package com.pwms.gateway.filter;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class JwtAuthFilter extends
        AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    @Value("${jwt.secret}")
    private String secret;

    public JwtAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();
            log.debug("Gateway request: {}", path);

            // ── Extract Authorization header ──────────────────
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            // ── No token provided ─────────────────────────────
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Missing or invalid Authorization header for: {}", path);
                return onError(exchange, HttpStatus.UNAUTHORIZED,
                        "Missing Authorization header");
            }

            String token = authHeader.substring(7);

            // ── Validate token ────────────────────────────────
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(getKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String username    = claims.getSubject();
                String role        = claims.get("role", String.class);
                String referenceId = String.valueOf(
                        claims.get("referenceId", Integer.class));

                log.debug("Authenticated: {} role: {}", username, role);

                // ── Forward user info to downstream service ───
                // Services can read these headers if needed
                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header("X-Auth-Username",    username)
                                .header("X-Auth-Role",        role)
                                .header("X-Auth-ReferenceId", referenceId)
                                .build())
                        .build();

                return chain.filter(mutatedExchange);

            } catch (ExpiredJwtException e) {
                log.warn("Token expired for path: {}", path);
                return onError(exchange, HttpStatus.UNAUTHORIZED, "Token expired");

            } catch (JwtException e) {
                log.warn("Invalid token for path: {}", path);
                return onError(exchange, HttpStatus.UNAUTHORIZED, "Invalid token");
            }
        };
    }

    // ── Return error response ─────────────────────────────────
    private Mono<Void> onError(ServerWebExchange exchange,
                               HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders()
                .add("Content-Type", "application/json");
        byte[] bytes = ("{\"error\":\"" + message + "\"}").getBytes();
        var buffer = exchange.getResponse()
                .bufferFactory().wrap(bytes);
        return exchange.getResponse()
                .writeWith(Mono.just(buffer));
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Config class required by AbstractGatewayFilterFactory
    public static class Config {
        // Empty — no config needed for this filter
    }
}