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
    return ResponseEntity.ok(toDto(user));
  }

  private Profile toDto(UserAccount user) {
    return new Profile()
        .isAdmin(userService.isUserAdmin(user))
        .username(user.getUsername())
        .pfp(user.getProfile().getProfilePictureUrl())
        .data(new ProfileData(user.getProfile().getDisplayName(), user.getMailAddr()));
  }

  @Override
  public ResponseEntity<Void> changeMyPassword(PasswordChangeRequest passwordChangeRequest) {
    userService.updatePassword(
        userService.currentUser(),
        passwordChangeRequest.getOldPassword(),
        passwordChangeRequest.getNewPassword());
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> deleteMyProfile() {
    userService.deleteUser(userService.currentUser());
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Profile> updateMyProfile(ProfileData profileData) {
    final var res = userService.updateUserData(
        userService.currentUser(),
        profileData.getName(),
        profileData.getEmail());
    return ResponseEntity.ok(toDto(res));
  }

  @Override
  public ResponseEntity<PfpUpdateResponse> updateProfilePic(MultipartFile file) {
    final var res = userService.updatePfp(userService.currentUser(), file);
    return ResponseEntity.ok(new PfpUpdateResponse().url(res));
  }

}
