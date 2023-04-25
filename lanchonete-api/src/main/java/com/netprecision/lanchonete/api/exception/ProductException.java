package com.netprecision.lanchonete.api.exception;

public class ProductException extends RuntimeException {
  private final String message;

  public ProductException(String message) {
    super(message);
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
