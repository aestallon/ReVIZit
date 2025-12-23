package org.revizit;

import java.util.Optional;
import org.revizit.persistence.entity.UserAccount;
import org.revizit.persistence.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class RevizitApplication {

  public static void main(String[] args) {
    SpringApplication.run(RevizitApplication.class, args);
  }

  @Bean
  CommandLineRunner runner(UserAccountRepository userAccountRepository,
                           PasswordEncoder passwordEncoder) {
    return args -> {
      userAccountRepository
          .findByUsername("foo")
          .ifPresentOrElse(
              it -> log.info("User {} is available", it.getUsername()),
              () -> {
                final var user = new UserAccount();
                user.setUserPw(passwordEncoder.encode("asd"));
                user.setUsername("foo");
                user.setMailAddr("foo@bar.com");
                user.setUserRole("ADMIN");
                user.setInactive(false);
                userAccountRepository.save(user);
                log.info("User {} is created", user.getUsername());
              });
    };
  }

}
