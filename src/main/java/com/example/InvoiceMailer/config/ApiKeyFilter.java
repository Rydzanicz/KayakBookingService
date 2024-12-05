package com.example.InvoiceMailer.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.io.IOException;

public class ApiKeyFilter implements Filter {

    @Value("${security.api.key}")
    private String apiKey;
    @Value("${security.api.role}")
    private String role;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            final String fullRequest = httpRequest.getHeader("X-API-KEY");

            if (fullRequest == null || fullRequest.isEmpty()) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("Unauthorized: Invalid or missing API Key");
                return;
            }

            final String requestApiKey = fullRequest.substring(6);
            if (fullRequest.startsWith(role) && apiKey.equals(requestApiKey)) {
                SecurityContextHolder.getContext()
                                     .setAuthentication(new PreAuthenticatedAuthenticationToken("apiKeyUser", null, null));
                chain.doFilter(request, response);
            } else {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("Unauthorized: Invalid API Key");
            }
        } catch (Exception e) {
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpResponse.getWriter().write("Internal Server Error: " + e.getMessage());
        }
    }



    @Override
    public void destroy() {
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
