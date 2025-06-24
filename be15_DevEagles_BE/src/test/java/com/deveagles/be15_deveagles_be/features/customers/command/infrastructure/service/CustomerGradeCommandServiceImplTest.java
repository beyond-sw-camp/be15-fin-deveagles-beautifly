package com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.CreateCustomerGradeRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.UpdateCustomerGradeRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.CustomerGrade;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.CustomerGradeRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("고객등급 커맨드 서비스 테스트")
class CustomerGradeCommandServiceImplTest {

  @Mock private CustomerGradeRepository customerGradeRepository;

  @InjectMocks private CustomerGradeCommandServiceImpl customerGradeCommandService;

  @Test
  @DisplayName("고객등급 생성 성공")
  void createCustomerGrade_Success() {
    // given
    String gradeName = "VIP";
    Integer discountRate = 10;
    Long shopId = 1L;
    CreateCustomerGradeRequest request =
        new CreateCustomerGradeRequest(shopId, gradeName, discountRate);

    CustomerGrade savedGrade =
        CustomerGrade.builder()
            .id(1L)
            .shopId(shopId)
            .customerGradeName(gradeName)
            .discountRate(discountRate)
            .build();

    given(customerGradeRepository.existsByCustomerGradeNameAndShopId(gradeName, shopId))
        .willReturn(false);
    given(customerGradeRepository.save(any(CustomerGrade.class))).willReturn(savedGrade);

    // when
    Long result = customerGradeCommandService.createCustomerGrade(request);

    // then
    assertThat(result).isEqualTo(1L);

    then(customerGradeRepository).should().existsByCustomerGradeNameAndShopId(gradeName, shopId);
    then(customerGradeRepository).should().save(any(CustomerGrade.class));
  }

  @Test
  @DisplayName("고객등급 생성 실패 - 중복된 등급명")
  void createCustomerGrade_DuplicateGradeName() {
    // given
    String gradeName = "VIP";
    Integer discountRate = 10;
    Long shopId = 1L;
    CreateCustomerGradeRequest request =
        new CreateCustomerGradeRequest(shopId, gradeName, discountRate);

    given(customerGradeRepository.existsByCustomerGradeNameAndShopId(gradeName, shopId))
        .willReturn(true);

    // when & then
    assertThatThrownBy(() -> customerGradeCommandService.createCustomerGrade(request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("이미 존재하는 고객등급명입니다.");

    then(customerGradeRepository).should().existsByCustomerGradeNameAndShopId(gradeName, shopId);
    then(customerGradeRepository).should(never()).save(any(CustomerGrade.class));
  }

  @Test
  @DisplayName("고객등급 수정 성공")
  void updateCustomerGrade_Success() {
    // given
    Long gradeId = 1L;
    Long shopId = 1L;
    String newGradeName = "VVIP";
    Integer newDiscountRate = 15;
    UpdateCustomerGradeRequest request =
        new UpdateCustomerGradeRequest(shopId, newGradeName, newDiscountRate);

    CustomerGrade existingGrade =
        CustomerGrade.builder()
            .id(gradeId)
            .shopId(shopId)
            .customerGradeName("VIP")
            .discountRate(10)
            .build();

    given(customerGradeRepository.findByIdAndShopId(gradeId, shopId))
        .willReturn(Optional.of(existingGrade));
    given(customerGradeRepository.existsByCustomerGradeNameAndShopId(newGradeName, shopId))
        .willReturn(false);

    // when
    customerGradeCommandService.updateCustomerGrade(gradeId, request);

    // then
    then(customerGradeRepository).should().findByIdAndShopId(gradeId, shopId);
    then(customerGradeRepository).should().existsByCustomerGradeNameAndShopId(newGradeName, shopId);
  }

  @Test
  @DisplayName("고객등급 수정 실패 - 존재하지 않는 등급")
  void updateCustomerGrade_GradeNotFound() {
    // given
    Long gradeId = 1L;
    Long shopId = 1L;
    UpdateCustomerGradeRequest request = new UpdateCustomerGradeRequest(shopId, "VVIP", 15);

    given(customerGradeRepository.findByIdAndShopId(gradeId, shopId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> customerGradeCommandService.updateCustomerGrade(gradeId, request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("고객등급을 찾을 수 없습니다.");

    then(customerGradeRepository).should().findByIdAndShopId(gradeId, shopId);
    then(customerGradeRepository).should(never()).existsByCustomerGradeNameAndShopId(any(), any());
  }

  @Test
  @DisplayName("고객등급 수정 실패 - 중복된 등급명")
  void updateCustomerGrade_DuplicateGradeName() {
    // given
    Long gradeId = 1L;
    Long shopId = 1L;
    String oldGradeName = "VIP";
    String newGradeName = "VVIP";
    Integer oldDiscountRate = 10;
    Integer newDiscountRate = 15;
    UpdateCustomerGradeRequest request =
        new UpdateCustomerGradeRequest(shopId, newGradeName, newDiscountRate);

    CustomerGrade existingGrade =
        CustomerGrade.builder()
            .id(gradeId)
            .shopId(shopId)
            .customerGradeName(oldGradeName)
            .discountRate(oldDiscountRate)
            .build();

    given(customerGradeRepository.findByIdAndShopId(gradeId, shopId))
        .willReturn(Optional.of(existingGrade));
    given(customerGradeRepository.existsByCustomerGradeNameAndShopId(newGradeName, shopId))
        .willReturn(true);

    // when & then
    assertThatThrownBy(() -> customerGradeCommandService.updateCustomerGrade(gradeId, request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("이미 존재하는 고객등급명입니다.");

    then(customerGradeRepository).should().findByIdAndShopId(gradeId, shopId);
    then(customerGradeRepository).should().existsByCustomerGradeNameAndShopId(newGradeName, shopId);
  }

  @Test
  @DisplayName("고객등급 수정 성공 - 동일한 등급명으로 수정시 중복 검사 스킵")
  void updateCustomerGrade_SameGradeNameSkipDuplicateCheck() {
    // given
    Long gradeId = 1L;
    Long shopId = 1L;
    String gradeName = "VIP";
    Integer oldDiscountRate = 10;
    Integer newDiscountRate = 15;
    UpdateCustomerGradeRequest request =
        new UpdateCustomerGradeRequest(shopId, gradeName, newDiscountRate);

    CustomerGrade existingGrade =
        CustomerGrade.builder()
            .id(gradeId)
            .shopId(shopId)
            .customerGradeName(gradeName)
            .discountRate(oldDiscountRate)
            .build();

    given(customerGradeRepository.findByIdAndShopId(gradeId, shopId))
        .willReturn(Optional.of(existingGrade));

    // when
    customerGradeCommandService.updateCustomerGrade(gradeId, request);

    // then
    then(customerGradeRepository).should().findByIdAndShopId(gradeId, shopId);
    then(customerGradeRepository).should(never()).existsByCustomerGradeNameAndShopId(any(), any());
  }

  @Test
  @DisplayName("고객등급 삭제 성공")
  void deleteCustomerGrade_Success() {
    // given
    Long gradeId = 1L;
    CustomerGrade existingGrade =
        CustomerGrade.builder()
            .id(gradeId)
            .shopId(1L)
            .customerGradeName("VIP")
            .discountRate(10)
            .build();

    given(customerGradeRepository.findById(gradeId)).willReturn(Optional.of(existingGrade));

    // when
    customerGradeCommandService.deleteCustomerGrade(gradeId);

    // then
    then(customerGradeRepository).should().findById(gradeId);
    then(customerGradeRepository).should().delete(existingGrade);
  }

  @Test
  @DisplayName("고객등급 삭제 실패 - 존재하지 않는 등급")
  void deleteCustomerGrade_GradeNotFound() {
    // given
    Long gradeId = 1L;
    given(customerGradeRepository.findById(gradeId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> customerGradeCommandService.deleteCustomerGrade(gradeId))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("고객등급을 찾을 수 없습니다.");

    then(customerGradeRepository).should().findById(gradeId);
    then(customerGradeRepository).should(never()).delete(any());
  }
}
