package org.revizit;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.revizit.persistence.entity.UserAccount;
import org.revizit.persistence.entity.WaterFlavour;
import org.revizit.persistence.entity.WaterState;
import org.revizit.persistence.repository.UserAccountRepository;
import org.revizit.persistence.repository.WaterFlavourRepository;
import org.revizit.persistence.repository.WaterStateRepository;
import org.revizit.rest.model.WaterFlavourDto;
import org.revizit.rest.model.WaterStateDto;
import org.revizit.service.UserService;
import org.revizit.service.WaterService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class RevizitApplication {

  public static void main(String[] args) {
    SpringApplication.run(RevizitApplication.class, args);
  }

  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }

  @Bean
  CommandLineRunner runner(UserAccountRepository userAccountRepository,
                           PasswordEncoder passwordEncoder) {
    return _ -> userAccountRepository
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
  }

}
