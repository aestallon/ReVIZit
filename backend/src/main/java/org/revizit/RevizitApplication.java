package org.revizit;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.revizit.persistence.entity.UserAccount;
import org.revizit.persistence.entity.WaterFlavour;
import org.revizit.persistence.entity.WaterState;
import org.revizit.persistence.repository.UserAccountRepository;
import org.revizit.persistence.repository.WaterFlavourRepository;
import org.revizit.persistence.repository.WaterStateRepository;
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
  @Order(1)
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

  @Bean
  @Order(2)
  CommandLineRunner runner2(WaterFlavourRepository flavourRepository) {
    return _ -> {
      final var flavourCount = flavourRepository.count();
      if (flavourCount > 0) {
        return;
      }

      log.info("Initialising default flavour...");
      final var flavour = new WaterFlavour();
      flavour.setName("Default");
      flavourRepository.save(flavour);
      log.info("Default flavour created.");
    };
  }

  @Bean
  @Order(3)
  CommandLineRunner runner3(UserService userService, WaterService waterService) {
    return _ -> {
      final var currState = waterService.currentState();
      if (currState != null) {
        return;
      }

      UserDetails foo = userService.loadUserByUsername("foo");
      SecurityContextHolder.getContext().setAuthentication(
          new UsernamePasswordAuthenticationToken(foo, null, foo.getAuthorities()));
      WaterState waterState = waterService.defineState(
          new WaterStateDto()
              .fullGallons(4)
              .emptyGallons(3)
              .reportedAt(OffsetDateTime.now())
              .waterLevel(76),
          1L);
      SecurityContextHolder.clearContext();
      log.info("Initialised water state: {}", waterState);
    };
  }

}
