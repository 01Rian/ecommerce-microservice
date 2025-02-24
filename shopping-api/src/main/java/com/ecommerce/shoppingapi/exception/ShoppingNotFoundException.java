package com.ecommerce.shoppingapi.exception;

public class ShoppingNotFoundException extends ResourceNotFoundException {
  private static final String RESOURCE_NAME = "Shopping";

  public ShoppingNotFoundException(String fieldName, Object fieldValue) {
      super(RESOURCE_NAME, fieldName, fieldValue);
  }
}
