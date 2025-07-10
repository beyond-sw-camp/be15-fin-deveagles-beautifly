package com.deveagles.be15_deveagles_be.features.notifications.command.application.service;

import com.deveagles.be15_deveagles_be.features.notifications.query.application.dto.NotificationResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
public class NotificationSseService {

  // SSE 타임아웃 시간 (1시간)
  private static final Long SSE_TIMEOUT = 1000L * 60 * 60;
  // 동시성 문제를 해결하기 위해 ConcurrentHashMap 사용
  private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

  /**
   * 사용자가 구독을 시작하면, SseEmitter를 생성하고 저장합니다.
   *
   * @param shopId 구독하는 사용자의 매장 ID
   * @return 생성된 SseEmitter 객체
   */
  public SseEmitter subscribe(Long shopId) {
    SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
    emitters.put(shopId, emitter);

    // 연결이 종료되거나 타임아웃되면 emitters 맵에서 제거
    emitter.onCompletion(() -> emitters.remove(shopId));
    emitter.onTimeout(() -> emitters.remove(shopId));
    emitter.onError(
        e -> {
          log.error("SSE Emitter error for shopId: {}", shopId, e);
          emitters.remove(shopId);
        });

    // 연결 직후, 더미 데이터를 보내 연결이 수립되었음을 클라이언트에게 알림
    sendToClient(emitter, "connect", "SSE 연결이 성공적으로 완료되었습니다. (shopId: " + shopId + ")");

    return emitter;
  }

  /**
   * 특정 매장(사용자)에게 알림을 보냅니다.
   *
   * @param shopId 알림을 받을 매장의 ID
   * @param notification 저장된 알림 객체
   */
  public void send(Long shopId, NotificationResponse notification) {
    if (emitters.containsKey(shopId)) {
      SseEmitter emitter = emitters.get(shopId);
      sendToClient(emitter, "notification", notification);
    }
  }

  /** 클라이언트에게 실제 데이터를 전송하는 헬퍼 메서드 */
  private void sendToClient(SseEmitter emitter, String eventName, Object data) {
    try {
      emitter.send(
          SseEmitter.event()
              .id(String.valueOf(System.currentTimeMillis())) // 이벤트 ID
              .name(eventName) // 클라이언트가 수신할 이벤트 이름
              .data(data)); // 실제 전송할 데이터 (JSON으로 변환됨)
    } catch (IOException e) {
      log.error("SSE 데이터 전송 중 오류 발생: {}", e.getMessage());
      emitter.complete(); // 오류 발생 시 연결 종료
    }
  }
}
