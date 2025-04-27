package com.loan.loan.security;

import org.springframework.security.core.AuthenticatedPrincipal;

public class UserPrincipal implements AuthenticatedPrincipal {
    private final Long userId;
    private final String username;

    public UserPrincipal(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return username;
    }
}