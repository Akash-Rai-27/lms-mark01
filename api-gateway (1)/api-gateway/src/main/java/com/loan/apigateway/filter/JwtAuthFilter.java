package com.loan.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    @Value("${jwt.secret}")
    private String secret;

    public JwtAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);
            try {
                SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String roles = claims.get("roles", String.class);
                if (config.getRequiredRoles() != null && !hasRequiredRole(roles, config.getRequiredRoles())) {
                    return onError(exchange, "Insufficient permissions", HttpStatus.FORBIDDEN);
                }

                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-Authenticated-User", claims.getSubject())
                        .header("X-Authenticated-Roles", roles)
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private boolean hasRequiredRole(String userRoles, List<String> requiredRoles) {
        if (userRoles == null || userRoles.isEmpty()) return false;
        return Arrays.stream(userRoles.split(","))
                .anyMatch(role -> requiredRoles.contains(role.trim()));
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {
        private List<String> requiredRoles;

        public List<String> getRequiredRoles() {
            return requiredRoles;
        }

        public void setRequiredRoles(List<String> requiredRoles) {
            this.requiredRoles = requiredRoles;
        }
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("requiredRoles");
    }

    @Override
    public String name() {
        return "JwtAuth";
    }
}