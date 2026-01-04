package org.revizit.rest.impl;

import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.revizit.persistence.entity.SysLog;
import org.revizit.rest.api.AuthApiDelegate;
import org.revizit.rest.model.AuthenticationRequest;
import org.revizit.rest.model.AuthenticationResponse;
import org.revizit.security.JwtService;
import org.revizit.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthApiDelegateImpl implements AuthApiDelegate {

  private final ApplicationEventPublisher eventPublisher;
  private final Clock clock;
  private final AuthenticationManager authenticationManager;
  private final UserService userService;
  private final JwtService jwtService;

  @Override
  public ResponseEntity<AuthenticationResponse> login(AuthenticationRequest authenticationRequest) {
    final var authentication =
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            authenticationRequest.getUsername(),
            authenticationRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    final var user = userService.currentUser();
    final String jwtToken = jwtService.generateToken(user, user.getId(), user.getUserRole());
    eventPublisher.publishEvent(SysLog.ofUserLogIn(user, LocalDateTime.now(clock)));
    return ResponseEntity.ok(new AuthenticationResponse().token(jwtToken));
  }

  @Override
  public ResponseEntity<Void> isAuthenticated() {
    return ResponseEntity.ok().build();
  }

}
