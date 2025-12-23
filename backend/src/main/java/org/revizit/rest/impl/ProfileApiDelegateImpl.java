package org.revizit.rest.impl;

import org.revizit.persistence.entity.UserAccount;
import org.revizit.rest.api.ProfileApiDelegate;
import org.revizit.rest.model.Profile;
import org.revizit.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileApiDelegateImpl implements ProfileApiDelegate {

  private final UserService userService;

  @Override
  public ResponseEntity<Profile> getMyProfile() {
    final UserAccount user = userService.currentUser();
    return ResponseEntity.ok(new Profile()
        .isAdmin(userService.isUserAdmin(user))
        .username(user.getUsername())
        // TODO: Store name and maybe other profile info?
        .name(user.getUsername()));
  }

}
