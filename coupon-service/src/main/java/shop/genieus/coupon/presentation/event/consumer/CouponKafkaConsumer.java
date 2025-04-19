package shop.genieus.coupon.presentation.event.consumer;

import static io.micrometer.tracing.Tracer.*;

import com.genieus.common.event.util.EventRouter;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponKafkaConsumer {

  private static final String SPAN_NAME = "kafka.consume";

  private final Tracer tracer;
  private final EventRouter eventRouter;

  @KafkaListener(topics = "${spring.kafka.consumer.topic.order}")
  public void consume(
      @Payload String payload,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(value = "traceId", required = false) String traceId,
      @Header(value = "spanId", required = false) String spanId) {
    Span consumerSpan = createConsumerSpan(traceId, spanId);
    try (SpanInScope scope = tracer.withSpan(consumerSpan)) {
      eventRouter.route(topic, payload);
    } catch (Exception e) {
      log.error("이벤트 라우팅 중 오류 발생: topic={}, error={}", topic, e.getMessage());
    } finally {
      consumerSpan.end();
    }
  }

  private Span createConsumerSpan(String traceId, String spanId) {
    if (traceId == null || spanId == null) {
      return tracer.nextSpan().name(SPAN_NAME).start();
    }
    TraceContext parentContext =
        tracer.traceContextBuilder().traceId(traceId).spanId(spanId).sampled(true).build();
    return tracer.spanBuilder().name(SPAN_NAME).setParent(parentContext).start();
  }
}
