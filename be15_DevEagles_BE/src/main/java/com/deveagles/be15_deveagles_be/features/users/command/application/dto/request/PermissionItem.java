package com.deveagles.be15_deveagles_be.features.users.command.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record PermissionItem(
    @NotNull Long accessId,
    @NotNull Boolean canRead,
    @NotNull Boolean canWrite,
    @NotNull Boolean canDelete,
    @NotNull Boolean active) {}
