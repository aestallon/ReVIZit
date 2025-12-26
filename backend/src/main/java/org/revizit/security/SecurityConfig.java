package org.revizit.security;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.revizit.persistence.repository.UserAccountRepository;
import org.revizit.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                 JwtAuthenticationFilter jwtAuthFilter,
                                                 AuthenticationManager authenticationManager,
                                                 UserService userService,
                                                 AuthenticationProvider authenticationProvider)
      throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/login", "/api/water/current", "/api/flavour").permitAll()
            .requestMatchers("/actuator/**").permitAll() // If needed
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authenticationManager(authenticationManager)
        .userDetailsService(userService)
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(it -> it
            .accessDeniedHandler((request, response, accessDeniedException) -> response.setStatus(
                HttpStatus.FORBIDDEN.value()))
            .addObjectPostProcessor(new ObjectPostProcessor<ExceptionTranslationFilter>() {

              @Override
              public <O extends ExceptionTranslationFilter> O postProcess(O object) {
                object.setAuthenticationTrustResolver(new AuthenticationTrustResolver() {
                  @Override
                  public boolean isAnonymous(@Nullable Authentication authentication) {
                    return false;
                  }

                  @Override
                  public boolean isRememberMe(@Nullable Authentication authentication) {
                    return false;
                  }
                });
                return object;
              }
            }));

    return http.build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder);
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
