package com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public record CustomerCreateRequest(
    Long shopId,
    String name,
    String phone,
    String gender,
    String birthdate,
    Long gradeId,
    Long staffId,
    Long channelId,
    List<Long> tags,
    String memo,
    Boolean marketingConsent,
    Boolean notificationConsent) {}
