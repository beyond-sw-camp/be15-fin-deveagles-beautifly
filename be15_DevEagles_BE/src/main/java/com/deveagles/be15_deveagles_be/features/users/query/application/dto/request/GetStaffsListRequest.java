package com.deveagles.be15_deveagles_be.features.users.query.application.dto.request;

public record GetStaffsListRequest(int page, int size, String keyword, Boolean isActive) {}
