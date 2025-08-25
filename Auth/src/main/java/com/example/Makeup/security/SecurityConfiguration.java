package com.example.Makeup.security;

import com.example.Makeup.service.impl.AccountServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
  @Autowired private AccountServiceImpl accountServiceImpl;

  @Autowired private JwtFilter jwtFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable()) // Disable CSRF protection for stateless APIs
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/login/**", "/register/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
        )
        .sessionManagement(
            session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS)) // Set session management to stateless
        .addFilterBefore(
            jwtFilter,
            UsernamePasswordAuthenticationFilter
                .class) // Add JWT filter before UsernamePasswordAuthenticationFilter
        .exceptionHandling(
            exceptionHandling ->
                exceptionHandling.accessDeniedHandler(
                    (request, response, accessDeniedException) -> {
                      response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
                      response.sendRedirect("/error/403");
                    }))
        .logout(
            logout ->
                logout
                    .logoutUrl("/logout")
                    .addLogoutHandler(new CustomLogoutHandler()) // Use custom logout handler
                    .logoutSuccessHandler(
                        (request, response, authentication) -> {
                          response.sendRedirect("/auth/login"); // Redirect to login page after logout
                        })
                    .permitAll() // Allow all users to access the logout endpoint
            )
        .build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
    provider.setUserDetailsService(accountServiceImpl);

    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


}
