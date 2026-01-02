package org.revizit.rest.impl;

import java.util.List;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import org.revizit.persistence.entity.UserAccount;
import org.revizit.rest.api.UserManagementApiDelegate;
import org.revizit.rest.model.Profile;
import org.revizit.rest.model.ProfileData;
import org.revizit.rest.model.UserSelector;
import org.revizit.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManagementApiDelegateImpl implements UserManagementApiDelegate {

  private final UserService userService;
  @Value("${revizit.default-password:asd}")
  private String defaultPassword;

  @Override
  public ResponseEntity<Void> createUsers(MultipartFile file) {
    return UserManagementApiDelegate.super.createUsers(file);
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
        .map(this::toDto)
        .collect(collectingAndThen(toList(), ResponseEntity::ok));
  }


  private Profile toDto(UserAccount user) {
    return new Profile()
        .isAdmin(userService.isUserAdmin(user))
        .username(user.getUsername())
        .pfp(user.getProfile().getProfilePictureUrl())
        .data(new ProfileData(user.getProfile().getDisplayName(), user.getMailAddr()));
  }

  @Override
  public ResponseEntity<Void> resetUserPassword(UserSelector userSelector) {
    final var user = (UserAccount) userService.loadUserByUsername(userSelector.getUsername());
    userService.updatePassword(user, defaultPassword);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Profile> updateUser(Profile profile) {
    var user = (UserAccount) userService.loadUserByUsername(profile.getUsername());
    user = userService.updateUserData(
        user,
        profile.getData().getName(),
        profile.getData().getEmail());
    return ResponseEntity.ok(toDto(user));
  }
}
