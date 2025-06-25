package com.deveagles.be15_deveagles_be.features.users.command.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record UserCreateRequest(
    @NotBlank(message = "아이디를 입력해주세요.") String loginId,
    @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:\";'<>?,./]).{8,}$",
            message = "비밀번호는 영문자, 숫자, 특수문자를 포함한 8자 이상이어야 합니다.")
        String password,
    @NotBlank(message = "이름을 입력해주세요.") String staffName,
    @NotBlank(message = "전화번호를 입력해주세요.") String phoneNumber,
    String grade,
    @Email @NotBlank(message = "이메일 주소를 입력해주세요.") String email,
    Long shopId) {}
