package com.deveagles.be15_deveagles_be.features.users.query.application.service;

import com.deveagles.be15_deveagles_be.common.dto.Pagination;
import com.deveagles.be15_deveagles_be.features.users.query.application.dto.response.StaffListInfo;
import com.deveagles.be15_deveagles_be.features.users.query.application.dto.response.StaffsListResponse;
import com.deveagles.be15_deveagles_be.features.users.query.infraStrucure.repository.StaffQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffQueryServiceImpl implements StaffQueryService {

  private final StaffQueryRepository staffQueryRepository;

  @Override
  public StaffsListResponse getStaff(
      Long shopId, int size, int page, String keyword, Boolean isActive) {

    Pageable pageable = PageRequest.of(page - 1, size, Sort.by("staffId").descending());
    Page<StaffListInfo> staffPage =
        staffQueryRepository.searchStaffs(shopId, keyword, isActive, pageable);

    return StaffsListResponse.builder()
        .staffList(staffPage.getContent())
        .pagination(
            Pagination.builder()
                .currentPage(page)
                .totalItems(staffPage.getTotalElements())
                .totalPages(staffPage.getTotalPages())
                .build())
        .build();
  }
}
