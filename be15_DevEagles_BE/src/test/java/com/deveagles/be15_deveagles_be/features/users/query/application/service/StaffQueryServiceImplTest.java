package com.deveagles.be15_deveagles_be.features.users.query.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.deveagles.be15_deveagles_be.features.users.query.application.dto.response.StaffListInfo;
import com.deveagles.be15_deveagles_be.features.users.query.application.dto.response.StaffsListResponse;
import com.deveagles.be15_deveagles_be.features.users.query.infraStrucure.repository.StaffQueryRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
@DisplayName("StaffQueryService 단위 테스트")
public class StaffQueryServiceImplTest {

  @InjectMocks private StaffQueryServiceImpl staffQueryService;

  @Mock private StaffQueryRepository staffQueryRepository;

  @Test
  @DisplayName("getStaff: 직원 목록 조회 성공")
  void getStaff_성공() {
    // given
    Long shopId = 1L;
    int size = 10;
    int page = 1;
    String keyword = "홍길동";
    Boolean isActive = true;

    List<StaffListInfo> staffList =
        List.of(
            StaffListInfo.builder().staffId(1L).staffName("홍길동").phoneNumber("01012345678").build(),
            StaffListInfo.builder()
                .staffId(2L)
                .staffName("김철수")
                .phoneNumber("01098765432")
                .build());

    Page<StaffListInfo> staffPage =
        new PageImpl<>(staffList, PageRequest.of(0, size), staffList.size());

    when(staffQueryRepository.searchStaffs(
            shopId, keyword, isActive, PageRequest.of(0, size, Sort.by("staffId").descending())))
        .thenReturn(staffPage);

    // when
    StaffsListResponse result = staffQueryService.getStaff(shopId, size, page, keyword, isActive);

    // then
    assertEquals(2, result.getStaffList().size());
    assertEquals("홍길동", result.getStaffList().get(0).getStaffName());
    assertEquals(page, result.getPagination().getCurrentPage());
    assertEquals(1, result.getPagination().getTotalPages());
    assertEquals(2, result.getPagination().getTotalItems());
  }
}
