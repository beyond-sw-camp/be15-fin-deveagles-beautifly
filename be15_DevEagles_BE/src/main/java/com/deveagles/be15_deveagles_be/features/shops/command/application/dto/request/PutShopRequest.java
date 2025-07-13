package com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PutShopRequest(
    @NotBlank(message = "상점명은 필수 입력값입니다.") String shopName,
    @NotBlank(message = "주소는 필수 입력값입니다.") String address,
    @NotBlank(message = "상세주소는 필수 입력값입니다.") String detailAddress,
    @NotNull(message = "업종은 필수 입력값입니다.") Long industryId,
    String phoneNumber,
    String bizNumber,
    String description,
    List<SnsRequest> snsList,
    List<Long> deletedSnsIds) {}
