package com.example.Makeup.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

    String refreshToken = extractCookie(request);
    String redisKey = "refreshToken:" + refreshToken;
    redisTemplate.delete(redisKey);
    log.info("Deleted refresh token in Redis with key={}", redisKey);

    Cookie cookie = new Cookie("refresh_token", null);
    cookie.setHttpOnly(true);
    cookie.setSecure(false);
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);

    log.info("User logged out, refresh token invalidated and cookie removed");
  }

  private String extractCookie(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (cookie.getName().equals("refresh_token")) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}
