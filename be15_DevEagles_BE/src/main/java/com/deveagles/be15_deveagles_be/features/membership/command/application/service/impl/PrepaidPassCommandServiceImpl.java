package com.deveagles.be15_deveagles_be.features.membership.command.application.service.impl;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.PrepaidPassRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.application.service.PrepaidPassCommandService;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.PrepaidPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.PrepaidPassRepository;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.ShopRepository;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrepaidPassCommandServiceImpl implements PrepaidPassCommandService {

  private final PrepaidPassRepository prepaidPassRepository;
  private final ShopRepository shopRepository;

  @Override
  public void registPrepaidPass(PrepaidPassRequest request) {

    if (request.getShopId() == null) {
      throw new BusinessException(ErrorCode.ITEMS_SHOP_NOT_FOUND);
    }
    if (Objects.isNull(request.getPrepaidPassName()) || request.getPrepaidPassName().isBlank()) {
      throw new BusinessException(ErrorCode.MEMBERSHIP_NAME_REQUIRED);
    }
    if (Objects.isNull(request.getPrepaidPassPrice()) || request.getPrepaidPassPrice() <= 0) {
      throw new BusinessException(ErrorCode.MEMBERSHIP_PRICE_REQUIRED);
    }
    if (Objects.isNull(request.getExpirationPeriod()) || request.getExpirationPeriod() <= 0) {
      throw new BusinessException(ErrorCode.MEMBERSHIP_EXPIRATION_PERIOD_REQUIRED);
    }
    if (Objects.isNull(request.getExpirationPeriodType())) {
      throw new BusinessException(ErrorCode.MEMBERSHIP_EXPIRATION_PERIOD_TYPE_REQUIRED);
    }

    Shop shop =
        shopRepository
            .findById(request.getShopId())
            .orElseThrow(() -> new BusinessException(ErrorCode.ITEMS_SHOP_NOT_FOUND));
    PrepaidPass prepaidPass =
        PrepaidPass.builder()
            .shopId(shop)
            .prepaidPassName(request.getPrepaidPassName())
            .prepaidPassPrice(request.getPrepaidPassPrice())
            .expirationPeriod(request.getExpirationPeriod())
            .expirationPeriodType(request.getExpirationPeriodType())
            .bonus(request.getBonus())
            .discountRate(request.getDiscountRate())
            .prepaidPassMemo(request.getPrepaidPassMemo())
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build();

    prepaidPassRepository.save(prepaidPass);
  }

  @Override
  public void updatePrepaidPass(Long prepaidPassId, PrepaidPassRequest request) {

    if (Objects.isNull(request.getPrepaidPassName()) || request.getPrepaidPassName().isBlank()) {
      throw new BusinessException(ErrorCode.MEMBERSHIP_NAME_REQUIRED);
    }
    if (Objects.isNull(request.getPrepaidPassPrice()) || request.getPrepaidPassPrice() <= 0) {
      throw new BusinessException(ErrorCode.MEMBERSHIP_PRICE_REQUIRED);
    }
    if (Objects.isNull(request.getExpirationPeriod()) || request.getExpirationPeriod() <= 0) {
      throw new BusinessException(ErrorCode.MEMBERSHIP_EXPIRATION_PERIOD_REQUIRED);
    }
    if (Objects.isNull(request.getExpirationPeriodType())) {
      throw new BusinessException(ErrorCode.MEMBERSHIP_EXPIRATION_PERIOD_TYPE_REQUIRED);
    }

    boolean shopExists = shopRepository.existsById(request.getShopId());
    if (!shopExists) {
      throw new BusinessException(ErrorCode.ITEMS_SHOP_NOT_FOUND);
    }
    PrepaidPass prepaidPass =
        prepaidPassRepository
            .findById(prepaidPassId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PREPAIDPASS_NOT_FOUND));

    // 필드 수정
    prepaidPass.updatePrepaidPass(
        request.getPrepaidPassName(),
        request.getPrepaidPassPrice(),
        request.getExpirationPeriod(),
        request.getExpirationPeriodType(),
        request.getBonus(),
        request.getDiscountRate(),
        request.getPrepaidPassMemo());

    prepaidPassRepository.save(prepaidPass);
  }

  @Override
  public void deletePrepaidPass(Long id) {
    PrepaidPass prepaidPass =
        prepaidPassRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.PREPAIDPASS_NOT_FOUND));

    prepaidPass.setDeletedAt();
    prepaidPassRepository.save(prepaidPass);
  }
}
