package com.deveagles.be15_deveagles_be.features.users.command.application.service;

import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.request.*;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.response.AccountResponse;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.response.ProfileResponse;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

public interface UserCommandService {
  Staff userRegist(@Valid UserCreateRequest userRequest, Long shopId);

  Boolean validCheckId(@Valid ValidCheckRequest validRequest);

  Boolean validCheckEmail(@Valid ValidCheckRequest validRequest);

  AccountResponse getAccount(@Valid GetAccountRequest accountRequest);

  AccountResponse patchAccount(@Valid PatchAccountRequest accountRequest);

  ProfileResponse getProfile(CustomUser customUser);

  ProfileResponse patchProfile(
      Long staffId, @Valid PatchProfileRequest profileRequest, MultipartFile profile);

  void patchPaassword(@Valid PatchPasswordRequest passwordRequest);
}
