package com.ecommerce.shoppingapi.exception;

import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException {
  private final String message;
  private final HttpStatus status;
  private final String errorCode;

  protected BaseException(String message, HttpStatus status, String errorCode) {
    super(message);
    this.message = message;
    this.status = status;
    this.errorCode = errorCode;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getErrorCode() {
    return errorCode;
  }
}
