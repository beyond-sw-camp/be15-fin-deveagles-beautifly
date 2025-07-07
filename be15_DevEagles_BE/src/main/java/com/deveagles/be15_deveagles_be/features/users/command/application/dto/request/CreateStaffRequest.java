package com.deveagles.be15_deveagles_be.features.users.command.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateStaffRequest(
    @NotBlank(message = "직원 이름은 필수 입력값입니다.") String staffName,
    @NotBlank(message = "아이디는 필수 입력값입니다.") String loginId,
    @NotBlank @Email(message = "이메일 주소는 필수 입력값입니다.") String email,
    @NotBlank(message = "비밀번호는 필수 입력값입니다.") String password,
    @NotBlank(message = "전화번호는 필수 입력값입니다.") String phoneNumber,
    @NotBlank(message = "직급은 필수 입력값입니다.") String grade) {}
