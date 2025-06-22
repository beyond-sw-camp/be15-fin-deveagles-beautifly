package com.deveagles.be15_deveagles_be.features.coupons.domain.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class CouponCodeGenerator {

  private static final String PREFIX = "CP";
  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  public String generateCouponCode() {
    String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
    String randomCode = generateSecureRandomString(8);

    return PREFIX + dateStr + randomCode;
  }

  public String generateCouponCode(String customPrefix) {
    String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
    String randomCode = generateSecureRandomString(8);

    return customPrefix + dateStr + randomCode;
  }

  public String generateShortCouponCode() {
    String randomCode = generateSecureRandomString(10);
    return PREFIX + randomCode;
  }

  private String generateSecureRandomString(int length) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < length; i++) {
      result.append(CHARACTERS.charAt(SECURE_RANDOM.nextInt(CHARACTERS.length())));
    }
    return result.toString();
  }
}
