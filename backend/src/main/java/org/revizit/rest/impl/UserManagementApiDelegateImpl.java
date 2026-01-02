package org.revizit.rest.impl;

import java.util.List;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import org.revizit.persistence.entity.UserAccount;
import org.revizit.rest.api.UserManagementApiDelegate;
import org.revizit.rest.model.Profile;
import org.revizit.rest.model.UserSelector;
import org.revizit.service.DataImportService;
import org.revizit.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManagementApiDelegateImpl implements UserManagementApiDelegate {

  private final UserService userService;
  private final DataImportService dataImportService;
  @Value("${revizit.default-password:asd}")
  private String defaultPassword;

  @Override
  public ResponseEntity<Void> createUsers(MultipartFile file) {
    final var imports = dataImportService.importUsersFromCsv(file);
    userService.createUsers(imports);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> deleteUser(UserSelector userSelector) {
    final var user = (UserAccount) userService.loadUserByUsername(userSelector.getUsername());
    userService.deleteUser(user);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<List<Profile>> getAllUsers() {
    return userService.activeUsers().stream()
        .map(userService::toDto)
        .collect(collectingAndThen(toList(), ResponseEntity::ok));
  }

  @Override
  public ResponseEntity<Void> resetUserPassword(UserSelector userSelector) {
    final var user = (UserAccount) userService.loadUserByUsername(userSelector.getUsername());
    userService.forceSetPassword(user, defaultPassword);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Profile> updateUser(Profile profile) {
    var user = (UserAccount) userService.loadUserByUsername(profile.getUsername());
    user = userService.updateUserData(
        user,
        profile.getData().getName(),
        profile.getData().getEmail());
    return ResponseEntity.ok(userService.toDto(user));
  }

  @Override
  @Transactional
  public ResponseEntity<Profile> flipUserRole(UserSelector userSelector) {
    var user = (UserAccount) userService.loadUserByUsername(userSelector.getUsername());
    final var admin = userService.isUserAdmin(user);
    user = userService.markAdmin(userSelector.getUsername(), !admin);
    return ResponseEntity.ok(userService.toDto(user));
  }
}
