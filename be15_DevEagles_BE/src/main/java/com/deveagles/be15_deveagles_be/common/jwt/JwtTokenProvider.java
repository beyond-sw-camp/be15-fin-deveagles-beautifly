package com.deveagles.be15_deveagles_be.common.jwt;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.expiration}")
  private long jwtExpiration;

  @Value("${jwt.refresh-expiration}")
  private long jwtRefreshExpiration;

  private SecretKey secretKey;

  private final RedisTemplate<String, String> redisTemplate;
  private final UserRepository userRepository;

  @PostConstruct
  public void init() {

    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    secretKey = Keys.hmacShaKeyFor(keyBytes);
  }

  public String createToken(String username) {

    Date now = new Date();
    Date expiration = new Date(now.getTime() + jwtExpiration);

    Staff staff =
        userRepository
            .findStaffByLoginId(username)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NAME_NOT_FOUND));

    // todo : 라우팅 페이지 별 접근권한 추가
    Map<String, String> map = new HashMap<>();
    map.put("type", "access");
    map.put("shopId", staff.getShopId().toString());
    map.put("username", staff.getLoginId());
    map.put("staffName", staff.getStaffName());
    map.put("userId", staff.getStaffId().toString());
    map.put("grade", staff.getGrade());
    map.put("profileUrl", staff.getProfileUrl());
    map.put("userStatus", staff.getStaffStatus().toString());

    return Jwts.builder()
        .subject(username)
        .issuedAt(now)
        .claims(map)
        .expiration(expiration)
        .signWith(secretKey)
        .compact();
  }

  public String createRefreshToken(String username) {

    Date now = new Date();
    Date expiration = new Date(now.getTime() + jwtRefreshExpiration);

    Staff staff =
        userRepository
            .findStaffByLoginId(username)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NAME_NOT_FOUND));

    // todo : 라우팅 페이지 별 접근권한 추가
    Map<String, String> map = new HashMap<>();
    map.put("type", "refresh");
    map.put("shopId", staff.getShopId().toString());
    map.put("username", staff.getLoginId());
    map.put("staffName", staff.getStaffName());
    map.put("userId", staff.getStaffId().toString());
    map.put("grade", staff.getGrade());
    map.put("profileUrl", staff.getProfileUrl());
    map.put("userStatus", staff.getStaffStatus().toString());

    return Jwts.builder()
        .subject(username)
        .issuedAt(now)
        .claims(map)
        .expiration(expiration)
        .signWith(secretKey)
        .compact();
  }

  public long getRefreshExpiration() {
    return jwtRefreshExpiration;
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
      return true;
    } catch (SecurityException | MalformedJwtException e) {
      throw new BadCredentialsException("Invalid JWT Token", e);
    } catch (ExpiredJwtException e) {
      throw new BadCredentialsException("Expired JWT Token", e);
    } catch (UnsupportedJwtException e) {
      throw new BadCredentialsException("Unsupported JWT Token", e);
    } catch (IllegalArgumentException e) {
      throw new BadCredentialsException("JWT Token claims empty", e);
    }
  }

  public String getUsernameFromJWT(String token) {
    Claims claims =
        Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    return claims.getSubject();
  }

  public long getRemainingExpiration(String token) {
    Claims claims = parseClaims(token);
    return claims.getExpiration().getTime() - System.currentTimeMillis();
  }

  public Claims parseClaims(String token) {
    try {
      return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    } catch (ExpiredJwtException e) {
      return e.getClaims(); // 만료된 토큰도 claims는 꺼낼 수 있음
    }
  }

  public boolean isRefreshToken(String token) {
    Claims claims = parseClaims(token);
    return "refresh".equals(claims.get("type", String.class));
  }

  public boolean isAccessTokenBlacklisted(String token) {
    return Boolean.TRUE.equals(redisTemplate.hasKey("BL:" + token));
  }
}
