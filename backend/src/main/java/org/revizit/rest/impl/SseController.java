package org.revizit.rest.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.revizit.event.PendingReportsChanged;
import org.revizit.event.WaterStateChanged;
import org.revizit.service.NotAuthorisedException;
import org.revizit.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

  private static final Logger log = LoggerFactory.getLogger(SseController.class);

  private final UserService userService;
  private final Map<String, List<SseEmitter>> emittersByUser = new ConcurrentHashMap<>();

  @GetMapping("/water")
  public SseEmitter subscribe() {
    final var user = userService.currentUser();
    if (user == null) {
      throw new NotAuthorisedException();
    }

    final var emitters =
        emittersByUser.computeIfAbsent(user.getUsername(), k -> new CopyOnWriteArrayList<>());
    final var emitter = new SseEmitter(Long.MAX_VALUE);
    emitter.onCompletion(() -> emitters.remove(emitter));
    emitter.onTimeout(() -> emitters.remove(emitter));
    emitter.onError(t -> {
      emitter.completeWithError(t);
      emitters.remove(emitter);
    });
    emitters.add(emitter);
    return emitter;
  }

  @EventListener(WaterStateChanged.class)
  @Async
  public void onWaterStateChanged(final WaterStateChanged event) {
    send("WATER_STATE_CHANGED");
  }

  @EventListener(PendingReportsChanged.class)
  @Async
  public void onPendingReportsChanged(final PendingReportsChanged event) {
    send("PENDING_REPORTS_CHANGED");
  }

  private void send(final String message) {
    final var id = UUID.randomUUID().toString();
    for (final var emitters : emittersByUser.values()) {
      final var deadEmitters = new ArrayList<SseEmitter>(emitters.size());
      emitters.forEach(emitter -> {

        try {
          emitter.send(SseEmitter.event()
              .name(message)
              .id(id)
              .data(message));
        } catch (final IOException e) {
          deadEmitters.add(emitter);
          log.error(e.getMessage(), e);
        }

      });
      emitters.removeAll(deadEmitters);
    }
  }
}
