package org.revizit.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import static java.util.stream.Collectors.toSet;
import org.jspecify.annotations.NonNull;
import org.revizit.persistence.entity.UserAccount;
import org.revizit.persistence.entity.UserProfile;
import org.revizit.persistence.repository.UserAccountRepository;
import org.revizit.rest.model.Profile;
import org.revizit.rest.model.ProfileData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  public static final String ROLE_PLAIN = "PLAIN";
  public static final String ROLE_ADMIN = "ADMIN";

  private final UserAccountRepository userAccountRepository;
  private final PasswordEncoder passwordEncoder;
  private final ImageStorageService imageStorageService;

  @Value("${revizit.default-password:asd}")
  private String defaultPassword;

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
  public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
    return userAccountRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
  }

  private boolean isCurrentUserAdminOrMatches(UserAccount user) {
    if (isCurrentUserAdmin()) {
      return true;
    }

    final var currentUser = currentUser();
    return currentUser != null && currentUser.getUsername().equals(user.getUsername());
  }

  @Transactional
  public UserAccount updatePassword(final UserAccount userAccount,
                                    final String oldPassword,
                                    final String newPassword) {
    if (!isCurrentUserAdminOrMatches(userAccount)) {
      throw new NotAuthorisedException();
    }

    final var currPassword = userAccount.getUserPw();
    if (!passwordEncoder.matches(oldPassword, currPassword)) {
      throw new IllegalArgumentException("Old password does not match!");
    }

    userAccount.setUserPw(passwordEncoder.encode(newPassword));
    return userAccountRepository.save(userAccount);
  }

  @Transactional
  public UserAccount forceSetPassword(final UserAccount userAccount, final String newPassword) {
    if (!isCurrentUserAdminOrMatches(userAccount)) {
      throw new NotAuthorisedException();
    }

    userAccount.setUserPw(passwordEncoder.encode(newPassword));
    return userAccountRepository.save(userAccount);
  }

  @Transactional
  public void deleteUser(UserAccount userAccount) {
    if (!isCurrentUserAdminOrMatches(userAccount)) {
      throw new NotAuthorisedException();
    }

    userAccount.setProfile(null);
    userAccount.setInactive(true);
    userAccount.setUsername("deleted-user-" + UUID.randomUUID());
    userAccount.setUserRole(ROLE_PLAIN);
    userAccount.setUserPw("not-set");
    userAccount.setMailAddr("not-set");
    userAccountRepository.save(userAccount);
  }

  @Transactional
  public UserAccount updateUserData(UserAccount userAccount, String displayName,
                                    String mailAddress) {
    if (!isCurrentUserAdminOrMatches(userAccount)) {
      throw new NotAuthorisedException();
    }

    final var oldDisplayName = userAccount.getProfile().getDisplayName();
    final var oldMailAddress = userAccount.getMailAddr();
    boolean changed = false;
    if (!Objects.equals(oldDisplayName, displayName)) {
      userAccount.getProfile().setDisplayName(displayName);
      changed = true;
    }
    if (!Objects.equals(oldMailAddress, mailAddress)) {
      userAccount.setMailAddr(mailAddress);
      changed = true;
    }

    if (changed) {
      return userAccountRepository.save(userAccount);
    } else {
      return userAccount;
    }
  }

  @Transactional
  public String updatePfp(UserAccount user, MultipartFile file) {
    if (!isCurrentUserAdminOrMatches(user)) {
      throw new NotAuthorisedException();
    }

    final String pfpPath = imageStorageService.storeImage(file);
    user.getProfile().setProfilePictureUrl(pfpPath);
    userAccountRepository.save(user);
    return pfpPath;
  }

  @Transactional(readOnly = true)
  public List<UserAccount> activeUsers() {
    if (!isCurrentUserAdmin()) {
      throw new NotAuthorisedException();
    }
    return userAccountRepository.findByInactive(false);
  }

  @Transactional(readOnly = true)
  public List<UserAccount> inactiveUsers() {
    return userAccountRepository.findByInactive(true);
  }

  @Transactional
  public void createUsers(List<DataImportService.UserImport> userImports) {
    if (!isCurrentUserAdmin()) {
      throw new NotAuthorisedException();
    }

    final Set<String> usernames = userImports.stream()
        .map(DataImportService.UserImport::username)
        .collect(toSet());
    if (usernames.isEmpty()) {
      return;
    }

    final Set<String> existingUsernames = userAccountRepository.findActiveByUsername(usernames)
        .stream().map(UserAccount::getUsername)
        .collect(toSet());
    if (existingUsernames.size() == usernames.size()) {
      return;
    }

    final var users = new ArrayList<UserAccount>(usernames.size());
    for (final var userImport : userImports) {
      final var username = userImport.username();
      final var mail = userImport.email();
      final var admin = userImport.admin();
      if (existingUsernames.contains(username)) {
        continue;
      }

      final var user = new UserAccount();
      user.setUsername(username);
      user.setUserPw(passwordEncoder.encode(defaultPassword));
      user.setMailAddr(mail);
      user.setUserRole(admin ? ROLE_ADMIN : ROLE_PLAIN);
      user.setInactive(false);

      final var profile = new UserProfile();
      profile.setDisplayName(username);
      user.setProfile(profile);
      users.add(user);
    }

    if (users.isEmpty()) {
      return;
    }

    userAccountRepository.saveAll(users);
  }

  @Transactional
  public UserAccount markAdmin(String username, boolean admin) {
    if (!isCurrentUserAdmin()) {
      throw new NotAuthorisedException();
    }

    final var roleToSet = admin ? ROLE_ADMIN : ROLE_PLAIN;
    final var user = (UserAccount) loadUserByUsername(username);
    if (roleToSet.equals(user.getUserRole())) {
      return user;
    }

    user.setUserRole(roleToSet);
    return userAccountRepository.save(user);
  }

  public Profile toDto(UserAccount user) {
    UserProfile profile = user.getProfile();
    final String pfp;
    final String displayName;
    if (profile == null) {
      pfp = null;
      displayName = user.getUsername();
    } else {
      pfp = profile.getProfilePictureUrl();
      displayName = profile.getDisplayName();
    }

    return new Profile()
        .isAdmin(isUserAdmin(user))
        .username(user.getUsername())
        .pfp(pfp)
        .data(new ProfileData(displayName, user.getMailAddr()));
  }

}
