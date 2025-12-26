package org.revizit;

import java.time.LocalDateTime;
import java.util.Optional;
import org.revizit.persistence.entity.UserAccount;
import org.revizit.persistence.entity.WaterFlavour;
import org.revizit.persistence.entity.WaterState;
import org.revizit.persistence.repository.UserAccountRepository;
import org.revizit.persistence.repository.WaterFlavourRepository;
import org.revizit.persistence.repository.WaterStateRepository;
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
  CommandLineRunner runner3(WaterStateRepository stateRepository) {
    return _ -> {
      long stateCount = stateRepository.count();
      if (stateCount > 0) {
        return;
      }
      log.info("Initialising default state...");
      final var state = new WaterState();
      state.setCurrPct(76);
      state.setEmptyCnt(3);
      state.setFullCnt(4);

      final var flavour = new WaterFlavour();
      flavour.setName("Default");
      flavour.setId(1);
      state.setCurrFlav(flavour);
      state.setCreatedAt(LocalDateTime.now());
      stateRepository.save(state);
      log.info("Default state created.");
    };
  }


}
