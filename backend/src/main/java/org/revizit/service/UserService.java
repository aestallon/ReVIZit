package org.revizit.service;

import org.jspecify.annotations.NonNull;
import org.revizit.persistence.entity.UserAccount;
import org.revizit.persistence.repository.UserAccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  public static final String ROLE_PLAIN = "PLAIN";
  public static final String ROLE_ADMIN = "ADMIN";

  private final UserAccountRepository userAccountRepository;

  public UserAccount currentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return null;
    }
    final var principal = authentication.getPrincipal();
    if (principal instanceof UserAccount userAccount) {
      return userAccount;
    }

    return null;
  }

  public boolean isCurrentUserAdmin() {
    final var user = currentUser();
    return isUserAdmin(user);
  }

  public boolean isUserAdmin(UserAccount user) {
    return user != null && ROLE_ADMIN.equals(user.getUserRole());
  }

  @Override
  public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
    return userAccountRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
  }

}
