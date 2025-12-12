package com.j3s.yobuddy.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class WebhookApiKeyFilter extends OncePerRequestFilter {

    private static final String SECRET_KEY = "your-secret-key";

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        // 웹훅 요청 URL만 검증 (예: /api/v1/admin/trainings/results)
        String path = request.getRequestURI();

        if ("/api/v1/webhook/trainings/results".equals(path)
            && "POST".equalsIgnoreCase(request.getMethod())) {

            String apiKey = request.getHeader("X-API-KEY");

            if (!SECRET_KEY.equals(apiKey)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Unauthorized: Invalid API Key");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
