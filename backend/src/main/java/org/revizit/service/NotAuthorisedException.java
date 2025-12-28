package org.revizit.service;

public class NotAuthorisedException extends RuntimeException {

  public NotAuthorisedException() {
    super("Insufficient privileges!");
  }

}
