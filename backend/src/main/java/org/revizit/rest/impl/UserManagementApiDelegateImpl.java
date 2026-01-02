package org.revizit.rest.impl;

import java.util.List;
import org.revizit.rest.api.UserManagementApiDelegate;
import org.revizit.rest.model.Profile;
import org.revizit.rest.model.UserSelector;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManagementApiDelegateImpl implements UserManagementApiDelegate {

  @Override
  public ResponseEntity<Void> createUsers(MultipartFile file) {
    return UserManagementApiDelegate.super.createUsers(file);
  }

  @Override
  public ResponseEntity<Void> deleteUser(UserSelector userSelector) {
    return UserManagementApiDelegate.super.deleteUser(userSelector);
  }

  @Override
  public ResponseEntity<List<Profile>> getAllUsers() {
    return UserManagementApiDelegate.super.getAllUsers();
  }

  @Override
  public ResponseEntity<Void> resetUserPassword(UserSelector userSelector) {
    return UserManagementApiDelegate.super.resetUserPassword(userSelector);
  }

  @Override
  public ResponseEntity<Profile> updateUser(Profile profile) {
    return UserManagementApiDelegate.super.updateUser(profile);
  }
}
