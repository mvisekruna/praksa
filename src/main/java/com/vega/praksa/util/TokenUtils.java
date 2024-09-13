package com.vega.praksa.util;

import com.vega.praksa.model.User;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class TokenUtils {

    @Value("${spring.application.name}")
    private String APP_NAME;

    @Value("${jwt.secret}")
    public String SECRET;

    @Value("${jwt.expires_in}")
    private int EXPIRES_IN;

    @Value("${jwt.auth_header}")
    private String AUTH_HEADER;

    private static final String AUDIENCE_WEB = "web";

    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;


    public String generateToken(String username) {
        String encodedSecret = Base64.getEncoder().encodeToString(SECRET.getBytes());

        return Jwts.builder()
                .setIssuer(APP_NAME)
                .setSubject(username)
                .setAudience(generateAudience())
                .setIssuedAt(new Date())
                .setExpiration(generateExpirationDate())
                .signWith(SIGNATURE_ALGORITHM, encodedSecret)
                .compact();
    }

    public String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER);
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = getAuthHeaderFromHeader(request);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
           return authHeader.substring(7);
        }

        return null;
    }

    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            username = claims != null ? claims.getSubject() : null;
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (JwtException e) {
            return null;
        }

        return username;
    }

    public Date getIssuedAtDateFromToken(String token) {
        Date issuedAt;

        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            issuedAt = claims != null ? claims.getIssuedAt() : null;
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (JwtException e) {
            return null;
        }

        return issuedAt;
    }

    public String getAudienceFromToken(String token) {
        String audience;

        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            audience = claims != null ? claims.getAudience() : null;
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (JwtException e) {
            return null;
        }

        return audience;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;

        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            expiration = claims != null ? claims.getExpiration() : null;
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (JwtException e) {
            return null;
        }

         return expiration;
    }

    public int getExpiredIn() {
        return EXPIRES_IN;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        User user = (User) userDetails;
        final String username = getUsernameFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);
        final Date expiration = getExpirationDateFromToken(token);

        return (username != null
                && username.equals(userDetails.getUsername())
                && !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate()))
                && expiration != null && !expiration.before(new Date());
    }


    private String generateAudience() {
        return AUDIENCE_WEB;
    }

    private Date generateExpirationDate() {
        return new Date(new Date().getTime() + EXPIRES_IN);
    }

    private Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            String encodedSecret = Base64.getEncoder().encodeToString(SECRET.getBytes());
            claims = Jwts.parser()
                    .setSigningKey(encodedSecret.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            claims = null;
        }

        return claims;
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return(lastPasswordReset != null && created.before(lastPasswordReset));
    }

}
