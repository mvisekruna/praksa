package com.vega.praksa.security.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Objects;

public class TokenBasedAuthentication extends AbstractAuthenticationToken {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String token;

    private final UserDetails principle;

    public TokenBasedAuthentication(UserDetails principle) {
        super(principle.getAuthorities());
        this.principle = principle;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public UserDetails getPrincipal() {
        return principle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenBasedAuthentication that = (TokenBasedAuthentication) o;
        return Objects.equals(token, that.token) && Objects.equals(principle, that.principle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, principle);
    }

}
