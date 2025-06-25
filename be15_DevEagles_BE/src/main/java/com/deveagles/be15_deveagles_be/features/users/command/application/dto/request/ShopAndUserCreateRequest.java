package com.deveagles.be15_deveagles_be.features.users.command.application.dto.request;

import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.ShopCreateRequest;
import jakarta.validation.Valid;

public record ShopAndUserCreateRequest(
    @Valid ShopCreateRequest shop, @Valid UserCreateRequest user) {}
