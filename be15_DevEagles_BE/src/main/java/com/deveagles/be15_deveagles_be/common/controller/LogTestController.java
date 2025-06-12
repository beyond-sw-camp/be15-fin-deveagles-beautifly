package com.deveagles.be15_deveagles_be.common.controller;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/log-test")
@Slf4j
public class LogTestController {

  @GetMapping("/debug/{message}")
  public Map<String, String> testDebugLog(@PathVariable String message) {
    log.debug("DEBUG 레벨 로그 테스트 - 메시지: {}", message);
    return Map.of("level", "DEBUG", "message", message);
  }

  @GetMapping("/info/{message}")
  public Map<String, String> testInfoLog(@PathVariable String message) {
    log.info("INFO 레벨 로그 테스트 - 메시지: {}", message);
    return Map.of("level", "INFO", "message", message);
  }

  @GetMapping("/warn/{message}")
  public Map<String, String> testWarnLog(@PathVariable String message) {
    log.warn("WARN 레벨 로그 테스트 - 메시지: {}", message);
    return Map.of("level", "WARN", "message", message);
  }

  @GetMapping("/error/{message}")
  public Map<String, String> testErrorLog(@PathVariable String message) {
    log.error("ERROR 레벨 로그 테스트 - 메시지: {}", message);
    return Map.of("level", "ERROR", "message", message);
  }

  @GetMapping("/exception")
  public Map<String, String> testExceptionLog() {
    try {
      throw new RuntimeException("테스트 예외 발생");
    } catch (Exception e) {
      log.error("예외 로그 테스트 - 예외 메시지: {}", e.getMessage(), e);
      return Map.of("level", "ERROR", "exception", e.getMessage());
    }
  }

  @GetMapping("/performance")
  public Map<String, String> testPerformanceLog() {
    long startTime = System.currentTimeMillis();

    log.info("성능 측정 시작");

    // 시뮬레이션을 위한 지연
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    log.info("성능 측정 완료 - 실행 시간: {}ms", duration);

    return Map.of("duration", duration + "ms");
  }
}
