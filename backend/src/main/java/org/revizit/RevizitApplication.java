package org.revizit;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.revizit.persistence.entity.UserAccount;
import org.revizit.persistence.entity.UserProfile;
import org.revizit.persistence.entity.WaterFlavour;
import org.revizit.persistence.entity.WaterState;
import org.revizit.persistence.repository.UserAccountRepository;
import org.revizit.persistence.repository.WaterFlavourRepository;
import org.revizit.persistence.repository.WaterStateRepository;
import org.revizit.rest.model.WaterFlavourDto;
import org.revizit.rest.model.WaterStateDto;
import org.revizit.service.UserService;
import org.revizit.service.WaterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
@EnableAsync
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
                           PasswordEncoder passwordEncoder,
                           @Value("${revizit.default-username:}") String username,
                           @Value("${revizit.default-password:}") String password) {
    return _ -> {
      if (username == null || username.isBlank() || password == null || password.isBlank()) {
        log.info("No default username or password provided");
        return;
      }

      userAccountRepository
          .findByUsername(username)
          .ifPresentOrElse(
              it -> log.info("User {} is available", it.getUsername()),
              () -> {
                final var user = new UserAccount();
                user.setUserPw(passwordEncoder.encode(password));
                user.setUsername(username);
                user.setMailAddr("");
                user.setUserRole(UserService.ROLE_ADMIN);
                user.setInactive(false);

                final var profile = new UserProfile();
                profile.setDisplayName(username);
                user.setProfile(profile);
                userAccountRepository.save(user);
                log.info("User {} is created", user.getUsername());
              });
    };
  }

}
