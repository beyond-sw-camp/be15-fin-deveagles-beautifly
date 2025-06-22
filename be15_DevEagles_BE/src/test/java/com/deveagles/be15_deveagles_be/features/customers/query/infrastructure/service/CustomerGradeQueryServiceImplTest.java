package com.deveagles.be15_deveagles_be.features.customers.query.infrastructure.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.CustomerGradeResponse;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.CustomerGrade;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.CustomerGradeRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("고객등급 쿼리 서비스 테스트")
class CustomerGradeQueryServiceImplTest {

  @Mock private CustomerGradeRepository customerGradeRepository;

  @InjectMocks private CustomerGradeQueryServiceImpl customerGradeQueryService;

  @Test
  @DisplayName("고객등급 단건 조회 성공")
  void getCustomerGrade_Success() {
    // given
    Long gradeId = 1L;
    CustomerGrade grade = createTestCustomerGrade(gradeId, "VIP", 10);

    given(customerGradeRepository.findById(gradeId)).willReturn(Optional.of(grade));

    // when
    CustomerGradeResponse response = customerGradeQueryService.getCustomerGrade(gradeId);

    // then
    assertThat(response.getId()).isEqualTo(gradeId);
    assertThat(response.getCustomerGradeName()).isEqualTo("VIP");
    assertThat(response.getDiscountRate()).isEqualTo(10);

    then(customerGradeRepository).should().findById(gradeId);
  }

  @Test
  @DisplayName("고객등급 단건 조회 실패 - 존재하지 않는 등급")
  void getCustomerGrade_NotFound() {
    // given
    Long gradeId = 1L;
    given(customerGradeRepository.findById(gradeId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> customerGradeQueryService.getCustomerGrade(gradeId))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("고객등급을 찾을 수 없습니다.");

    then(customerGradeRepository).should().findById(gradeId);
  }

  @Test
  @DisplayName("전체 고객등급 목록 조회 성공")
  void getAllCustomerGrades_Success() {
    // given
    List<CustomerGrade> grades =
        List.of(
            createTestCustomerGrade(1L, "일반", 0),
            createTestCustomerGrade(2L, "VIP", 10),
            createTestCustomerGrade(3L, "VVIP", 20));

    given(customerGradeRepository.findAll()).willReturn(grades);

    // when
    List<CustomerGradeResponse> responses = customerGradeQueryService.getAllCustomerGrades();

    // then
    assertThat(responses).hasSize(3);
    assertThat(responses.get(0).getCustomerGradeName()).isEqualTo("일반");
    assertThat(responses.get(0).getDiscountRate()).isEqualTo(0);
    assertThat(responses.get(1).getCustomerGradeName()).isEqualTo("VIP");
    assertThat(responses.get(1).getDiscountRate()).isEqualTo(10);
    assertThat(responses.get(2).getCustomerGradeName()).isEqualTo("VVIP");
    assertThat(responses.get(2).getDiscountRate()).isEqualTo(20);

    then(customerGradeRepository).should().findAll();
  }

  @Test
  @DisplayName("전체 고객등급 목록 조회 - 빈 목록")
  void getAllCustomerGrades_EmptyList() {
    // given
    given(customerGradeRepository.findAll()).willReturn(List.of());

    // when
    List<CustomerGradeResponse> responses = customerGradeQueryService.getAllCustomerGrades();

    // then
    assertThat(responses).isEmpty();

    then(customerGradeRepository).should().findAll();
  }

  @Test
  @DisplayName("고객등급 페이징 조회 성공")
  void getCustomerGrades_Success() {
    // given
    Pageable pageable = PageRequest.of(0, 2);
    List<CustomerGrade> grades =
        List.of(createTestCustomerGrade(1L, "일반", 0), createTestCustomerGrade(2L, "VIP", 10));
    Page<CustomerGrade> gradePage = new PageImpl<>(grades, pageable, 3);

    given(customerGradeRepository.findAll(pageable)).willReturn(gradePage);

    // when
    Page<CustomerGradeResponse> responses = customerGradeQueryService.getCustomerGrades(pageable);

    // then
    assertThat(responses.getContent()).hasSize(2);
    assertThat(responses.getTotalElements()).isEqualTo(3);
    assertThat(responses.getTotalPages()).isEqualTo(2);
    assertThat(responses.getNumber()).isEqualTo(0);
    assertThat(responses.getSize()).isEqualTo(2);

    assertThat(responses.getContent().get(0).getCustomerGradeName()).isEqualTo("일반");
    assertThat(responses.getContent().get(0).getDiscountRate()).isEqualTo(0);
    assertThat(responses.getContent().get(1).getCustomerGradeName()).isEqualTo("VIP");
    assertThat(responses.getContent().get(1).getDiscountRate()).isEqualTo(10);

    then(customerGradeRepository).should().findAll(pageable);
  }

  @Test
  @DisplayName("고객등급 페이징 조회 - 빈 페이지")
  void getCustomerGrades_EmptyPage() {
    // given
    Pageable pageable = PageRequest.of(0, 10);
    Page<CustomerGrade> emptyPage = new PageImpl<>(List.of(), pageable, 0);

    given(customerGradeRepository.findAll(pageable)).willReturn(emptyPage);

    // when
    Page<CustomerGradeResponse> responses = customerGradeQueryService.getCustomerGrades(pageable);

    // then
    assertThat(responses.getContent()).isEmpty();
    assertThat(responses.getTotalElements()).isEqualTo(0);
    assertThat(responses.getTotalPages()).isEqualTo(0);

    then(customerGradeRepository).should().findAll(pageable);
  }

  private CustomerGrade createTestCustomerGrade(Long id, String gradeName, Integer discountRate) {
    return CustomerGrade.builder()
        .id(id)
        .customerGradeName(gradeName)
        .discountRate(discountRate)
        .build();
  }
}
