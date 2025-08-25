package com.example.Makeup.security;

import com.example.Makeup.dto.model.UserDTO;
import com.example.Makeup.entity.RefreshToken;
import com.example.Makeup.service.IUserService;
import com.example.Makeup.service.common.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JWTProvider jwtProvider;
  private final RefreshTokenService refreshTokenService;
  private final IUserService userService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = resolveToken(request);
    if (token == null) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String username = null;

      if (jwtProvider.isTokenValid(token)) {
        username = jwtProvider.extractUserName(token);
      } else {
        username = jwtProvider.extractUserName(token); // re-extract username after refreshing token
      }

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        setupAuthenticationContext(token, username, request);
      }

    } catch (Exception e) {
      log.warn("Cannot set user authentication: {}", e.getMessage());
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }

    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("jwt".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  private void setupAuthenticationContext(
      String token, String username, HttpServletRequest request) {
    String role = jwtProvider.extractRole(token);
    List<GrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority(role));

    UserDTO userDTO = userService.loadUserDTOByUsername(username);

    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(userDTO, null, authorities);
    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }

}
