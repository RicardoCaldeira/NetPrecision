package com.netprecision.lanchonete.api.exception;

public class OrderException extends RuntimeException {
  private final String message;

  public OrderException(String message) {
    super(message);
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
