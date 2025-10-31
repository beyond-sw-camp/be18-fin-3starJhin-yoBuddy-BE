package com.j3s.yobuddy.common.security;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.j3s.yobuddy.common.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final JwtProperties props;

    public JwtTokenProvider(JwtProperties props) {
        this.props = props;
        if (props.getSecret() == null || props.getSecret().isBlank()) {
            throw new IllegalStateException("JWT secret is not configured. Set env JWT_SECRET_BASE64 or configure app.jwt.secret in application.yml");
        }
        String secret = Objects.requireNonNull(props.getSecret(),"app.jwt.secret is null");
        // Keys.hmacShaKeyFor requires sufficient length (>=256 bits) secret
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String createAccessToken(Long userId, String email, String role) {
        Instant now = Instant.now();
        Date iat = Date.from(now);
        Date exp = Date.from(now.plusMillis(props.getAccessExpirationMs()));

        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .claim("email", email)
            .claim("role", role)
            .setIssuer(props.getIssuer())
            .setIssuedAt(iat)
            .setExpiration(exp)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public String createRefreshToken(Long userId) {
        Instant now = Instant.now();
        Date iat = Date.from(now);
        Date exp = Date.from(now.plusMillis(props.getRefreshExpirationMs()));

        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuer(props.getIssuer())
            .setIssuedAt(iat)
            .setExpiration(exp)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public Long getUserIdFromToken(String token) {
        Jws<Claims> claims = parseClaims(token);
        String sub = claims.getBody().getSubject();
        return sub == null ? null : Long.valueOf(sub);
    }

    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
