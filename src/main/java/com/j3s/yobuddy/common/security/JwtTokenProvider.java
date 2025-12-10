package com.j3s.yobuddy.common.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.j3s.yobuddy.common.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final JwtProperties props;

    public JwtTokenProvider(JwtProperties props) {
        this.props = props;
        String secret = Objects.requireNonNull(props.getSecret(), "JWT secret is null");
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String createAccessToken(Long userId, String email, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .claim("email", email)
            .claim("role", role)
            .setIssuer(props.getIssuer())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusMillis(props.getAccessExpirationMs())))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public String createRefreshToken(Long userId) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuer(props.getIssuer())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusMillis(props.getRefreshExpirationMs())))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        return Long.valueOf(parseClaims(token).getBody().getSubject());
    }

    public String getEmail(String token) {
        return parseClaims(token).getBody().get("email", String.class);
    }

    public String getRole(String token) {
        return parseClaims(token).getBody().get("role", String.class);
    }

    public Authentication getAuthentication(String token) {
        Claims body = parseClaims(token).getBody();
        String role = body.get("role", String.class);
        Collection<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role)
        );
        return new UsernamePasswordAuthenticationToken(body.getSubject(), token, authorities);
    }


    public String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("ACCESS_TOKEN".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public Long getUserIdFromRequest(HttpServletRequest request) {
        String token = extractTokenFromCookie(request);
        if (!StringUtils.hasText(token) || !validate(token)) return null;

        try {
            return getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
}
