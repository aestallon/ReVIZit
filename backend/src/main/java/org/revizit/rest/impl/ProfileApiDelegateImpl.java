package org.revizit.rest.impl;

import org.revizit.persistence.entity.UserAccount;
import org.revizit.rest.api.ProfileApiDelegate;
import org.revizit.rest.model.PasswordChangeRequest;
import org.revizit.rest.model.PfpUpdateResponse;
import org.revizit.rest.model.Profile;
import org.revizit.rest.model.ProfileData;
import org.revizit.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
        .data(new ProfileData(user.getProfile().getDisplayName(), user.getMailAddr())));
  }

  @Override
  public ResponseEntity<Void> changeMyPassword(PasswordChangeRequest passwordChangeRequest) {
    return ProfileApiDelegate.super.changeMyPassword(passwordChangeRequest);
  }

  @Override
  public ResponseEntity<Void> deleteMyProfile() {
    return ProfileApiDelegate.super.deleteMyProfile();
  }

  @Override
  public ResponseEntity<Profile> updateMyProfile(ProfileData profileData) {
    return ProfileApiDelegate.super.updateMyProfile(profileData);
  }

  @Override
  public ResponseEntity<PfpUpdateResponse> updateProfilePic(MultipartFile file) {
    return ProfileApiDelegate.super.updateProfilePic(file);
  }

}
