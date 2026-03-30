package io.dirmon.project.auth.controller;

import io.dirmon.project.auth.dto.SignInRequest;
import io.dirmon.project.auth.dto.SignUpRequest;
import io.dirmon.project.auth.service.AuthService;
import io.dirmon.project.auth.service.JWTService;
import io.dirmon.project.common.dto.GenericResponse;
import io.dirmon.project.auth.exception.CookieNotFoundException;
import io.dirmon.project.user.model.UserModel;
import io.dirmon.project.user.service.UserService;
import io.dirmon.project.util.TimeProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JWTService jwtService;

    @Autowired
    public AuthController(
            AuthService authService,
            UserService userService,
            JWTService jwtService
    ) {
        this.authService = authService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<@NonNull GenericResponse<?>> signUp(
            @NonNull HttpServletRequest httpServletRequest,
            @RequestBody SignUpRequest signUpRequest,
            @NonNull HttpServletResponse httpServletResponse
    ) {
        UserModel userEntity = this.authService.signUp(signUpRequest);

        String accessToken = this.jwtService.generateAccessToken(userEntity);
        Instant accessTokenExpire = this.jwtService.extractExpiration(accessToken);

        this.setCookie(
                httpServletResponse,
                "ACCESS_TOKEN",
                accessToken,
                true,
                false,
                "Lax",
                "/",
                TimeProvider.convertInstantToDate(accessTokenExpire)
        );

        String refreshToken = this.jwtService.generateRefreshToken(userEntity);
        Instant refreshTokenExpire = this.jwtService.extractExpiration(refreshToken);

        this.setCookie(
                httpServletResponse,
                "REFRESH_TOKEN",
                refreshToken,
                true,
                false,
                "Lax",
                "/",
                TimeProvider.convertInstantToDate(refreshTokenExpire)
        );

        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "User was signed up successfully",
                null
        );
    }

    @PostMapping("/sign-in")
    public ResponseEntity<@NonNull GenericResponse<?>> signIn(
            @NonNull HttpServletRequest httpServletRequest,
            @RequestBody SignInRequest signInRequest,
            @NonNull HttpServletResponse httpServletResponse
    ) {
        UserModel userEntity = this.authService.signIn(signInRequest);

        this.jwtService.revokeAllTokensByUser(userEntity);

        String accessToken = this.jwtService.generateAccessToken(userEntity);
        Instant accessTokenExpire = this.jwtService.extractExpiration(accessToken);

        this.setCookie(
                httpServletResponse,
                "ACCESS_TOKEN",
                accessToken,
                true,
                false,
                "Lax",
                "/",
                TimeProvider.convertInstantToDate(accessTokenExpire)
        );

        String refreshToken = this.jwtService.generateRefreshToken(userEntity);
        Instant refreshTokenExpire = this.jwtService.extractExpiration(refreshToken);

        this.setCookie(
                httpServletResponse,
                "REFRESH_TOKEN",
                refreshToken,
                true,
                false,
                "Lax",
                "/",
                TimeProvider.convertInstantToDate(refreshTokenExpire)
        );

        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "User was signed in successfully",
                null
        );
    }

    @PostMapping("/sign-out")
    public ResponseEntity<@NonNull GenericResponse<?>> signOut(
            @NonNull HttpServletRequest httpServletRequest,
            @NonNull HttpServletResponse httpServletResponse
    ) {
        Cookie refreshTokenCookie = this.getCookie(httpServletRequest, "REFRESH_TOKEN");
        String refreshToken = refreshTokenCookie.getValue();

        String refreshTokenSubject = this.jwtService.extractSubject(refreshToken);

        UUID userId = UUID.fromString(refreshTokenSubject);
        UserModel userEntity = this.userService.fetchUserByUserId(userId);

        this.jwtService.revokeAllTokensByUser(userEntity);

        this.setCookie(
                httpServletResponse,
                "ACCESS_TOKEN",
                "",
                true,
                false,
                "Lax",
                "/",
                new Date()
        );

        this.setCookie(
                httpServletResponse,
                "REFRESH_TOKEN",
                "",
                true,
                false,
                "Lax",
                "/",
                new Date()
        );

        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "User was signed out successfully",
                null
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<@NonNull GenericResponse<?>> refresh(
            @NonNull HttpServletRequest httpServletRequest,
            @NonNull HttpServletResponse httpServletResponse
    ) {
        Cookie refreshTokenCookie = this.getCookie(httpServletRequest, "REFRESH_TOKEN");
        String refreshTokenCookieValue = refreshTokenCookie.getValue();

        Claims refreshTokenClaims = this.jwtService.validateRefreshToken(refreshTokenCookieValue);

        String id = refreshTokenClaims.getId();
        UUID tokenId = UUID.fromString(id);

        this.jwtService.revokeTokenByTokenId(tokenId);

        String subject = refreshTokenClaims.getSubject();
        UUID userId = UUID.fromString(subject);

        UserModel userEntity = this.userService.fetchUserByUserId(userId);

        String accessToken = this.jwtService.generateAccessToken(userEntity);
        Instant accessTokenExpire = this.jwtService.extractExpiration(accessToken);

        this.setCookie(
                httpServletResponse,
                "ACCESS_TOKEN",
                accessToken,
                true,
                false,
                "Lax",
                "/",
                TimeProvider.convertInstantToDate(accessTokenExpire)
        );

        String refreshToken = this.jwtService.generateRefreshToken(userEntity);
        Instant refreshTokenExpire = this.jwtService.extractExpiration(refreshToken);

        this.setCookie(
                httpServletResponse,
                "REFRESH_TOKEN",
                refreshToken,
                true,
                false,
                "Lax",
                "/",
                TimeProvider.convertInstantToDate(refreshTokenExpire)
        );

        return GenericResponse.genericResponse(
                HttpStatus.OK,
                "Credentials were refreshed successfully",
                null
        );
    }

    private Cookie getCookie(
            @NonNull HttpServletRequest request,
            String name
    ) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new CookieNotFoundException("Cookie was not found");
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new CookieNotFoundException("Cookie was not found"));
    }

    private void setCookie(
            @NonNull HttpServletResponse httpServletResponse,
            String name,
            String value,
            boolean httpOnly,
            boolean secure,
            String sameSite,
            String path,
            Date maxAge
    ) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(httpOnly)
                .secure(secure)
                .sameSite(sameSite)
                .path(path)
                .maxAge(Math.max((maxAge.getTime() - System.currentTimeMillis()) / 1000, 0))
                .build();

        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
