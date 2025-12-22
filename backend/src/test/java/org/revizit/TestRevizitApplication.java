package org.revizit;

import org.springframework.boot.SpringApplication;

public class TestRevizitApplication {

  public static void main(String[] args) {
    SpringApplication.from(RevizitApplication::main).with(TestcontainersConfiguration.class)
        .run(args);
  }

}
