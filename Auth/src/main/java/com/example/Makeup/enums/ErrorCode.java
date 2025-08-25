package com.example.Makeup.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public enum ErrorCode {

  // ===== AUTH & TOKEN =====
  AUTH_REFRESH_TOKEN_NOT_FOUND("Refresh token not found!", HttpStatus.NOT_FOUND),
  AUTH_REFRESH_TOKEN_EXPIRED("Refresh token expired!", HttpStatus.UNAUTHORIZED),


  // ===== USER =====
  USER_NOT_FOUND("User not found!", HttpStatus.NOT_FOUND),
  USER_NOT_EXISTED("User not existed!", HttpStatus.NOT_FOUND),
  USER_ALREADY_EXISTED("User already existed!", HttpStatus.CONFLICT),
  USER_PASSWORD_NOT_MATCH("Your username or password may be incorrect!", HttpStatus.BAD_REQUEST),
  USER_IN_AUTHENTICATED_NOT_FOUND("User in authenticated not found!", HttpStatus.NOT_FOUND),


  // ===== COMMON =====
  COMMON_RESOURCE_NOT_FOUND("Resource can't not found!", HttpStatus.NOT_FOUND),
  COMMON_RESOURCE_ALREADY_EXISTED("Already existed!", HttpStatus.CONFLICT),
  COMMON_IS_EMPTY("Empty!", HttpStatus.NO_CONTENT),

  // ===== DEFAULT / UNKNOWN =====
  UNKNOWN_ERROR("Unknown error code", HttpStatus.INTERNAL_SERVER_ERROR),
  INTERNAL_SERVER_ERROR("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

  String message;
  HttpStatusCode statusCode;
}
