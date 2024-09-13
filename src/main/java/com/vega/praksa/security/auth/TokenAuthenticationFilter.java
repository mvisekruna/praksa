package com.vega.praksa.security.auth;

import com.vega.praksa.util.TokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenUtils tokenUtils;

    private final UserDetailsService userDetailsService;

    protected final Log Logger = LogFactory.getLog(getClass());

    @Autowired
    public TokenAuthenticationFilter(TokenUtils tokenUtils, UserDetailsService userDetailsService) {
        this.tokenUtils = tokenUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authToken = tokenUtils.getToken(request);

        if(authToken == null) {
            Logger.debug("No token found, proceeding with unauthenticated request.");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = tokenUtils.getUsernameFromToken(authToken);
            if(username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (Boolean.TRUE.equals(tokenUtils.validateToken(authToken, userDetails))) {
                    TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
                    authentication.setToken(authToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (ExpiredJwtException ex) {
            Logger.debug("Token expired!");
        } catch (JwtException | IllegalArgumentException ex) {
            Logger.warn("Invalid JWT token", ex);
        }

        filterChain.doFilter(request, response);

    }
}
