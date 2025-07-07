package com.deveagles.be15_deveagles_be.features.users.command.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.sql.Date;
import java.util.List;

public record PutStaffRequest(
    @NotBlank(message = "직원명은 필수 입력값입니다.") String staffName,
    @NotBlank @Email(message = "이메일은 필수 입력값입니다.") String email,
    @NotBlank(message = "전화번호는 필수 입력값입니다.") String phoneNumber,
    @NotBlank(message = "직급은 필수 입력값입니다.") String grade,
    Date joinedDate,
    Date leftDate,
    String description,
    String colorCode,
    List<PermissionItem> permissions) {}
