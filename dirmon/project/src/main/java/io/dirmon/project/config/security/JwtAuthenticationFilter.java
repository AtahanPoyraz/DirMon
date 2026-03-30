package io.dirmon.project.config.security;

import io.dirmon.project.auth.service.JWTService;
import io.dirmon.project.user.model.UserRole;
import io.dirmon.project.common.logging.ApplicationLogger;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JWTService jwtService;


    @Autowired
    public JwtAuthenticationFilter(
            JWTService jwtService
    ) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest httpServletRequest,
            @NonNull HttpServletResponse httpServletResponse,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            Cookie accessTokenCookie = getCookie(httpServletRequest, "ACCESS_TOKEN");
            if (accessTokenCookie == null) {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }

            String accessToken = accessTokenCookie.getValue();
            if (!StringUtils.hasText(accessToken)) {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }

            Claims accessTokenClaims = this.jwtService.validateAccessToken(accessToken);

            Boolean enabledClaim = accessTokenClaims.get("enabled", Boolean.class);
            boolean enabled = Boolean.TRUE.equals(enabledClaim);

            if (!enabled) {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }

            List<?> rolesClaim = accessTokenClaims.get("roles", List.class);
            List<UserRole> roles = rolesClaim.stream()
                    .map(String::valueOf)
                    .map(UserRole::valueOf)
                    .toList();

            if (roles.isEmpty()) {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }

            String subject = accessTokenClaims.getSubject();
            UUID userId = UUID.fromString(subject);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    roles
            );

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            filterChain.doFilter(httpServletRequest, httpServletResponse);

        } catch (Exception e) {
            ApplicationLogger.warn(
                    JwtAuthenticationFilter.class,
                    String.format("Access token parsing FAILED in JwtAuthenticationFilter: %s", e.getMessage())
            );

            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private Cookie getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst()
                .orElse(null);
    }
}
