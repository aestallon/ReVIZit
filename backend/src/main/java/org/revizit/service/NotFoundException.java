package org.revizit.service;

public class NotFoundException extends RuntimeException {

  public NotFoundException(final String msg) {
    super(msg);
  }

}
