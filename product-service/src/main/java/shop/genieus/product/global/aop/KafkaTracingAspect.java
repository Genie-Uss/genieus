package shop.genieus.product.global.aop;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Tracer.SpanInScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTracingAspect {

  private static final String SPAN_NAME = "kafka-consume";
  private final Tracer tracer;

  @Around("@annotation(org.springframework.kafka.annotation.KafkaListener) && args(message)")
  public Object traceKafkaMessage(ProceedingJoinPoint pjp, Message<String> message)
      throws Throwable {
    MessageHeaders headers = message.getHeaders();
    String traceId = headers.get("traceId", String.class);
    String spanId = headers.get("spanId", String.class);

    return traceWithSpan(pjp, traceId, spanId);
  }

  @Around(
      "@annotation(org.springframework.kafka.annotation.KafkaListener) && "
          + "execution(* *(@org.springframework.messaging.handler.annotation.Payload (*), ..)) && "
          + "!args(org.springframework.messaging.Message)")
  public Object traceKafkaMessageWithPayload(ProceedingJoinPoint pjp) throws Throwable {
    return traceWithSpan(pjp, null, null);
  }

  private Object traceWithSpan(ProceedingJoinPoint pjp, String traceId, String spanId)
      throws Throwable {
    Span span = createConsumerSpan(traceId, spanId);
    try (SpanInScope scope = tracer.withSpan(span)) {
      return pjp.proceed();
    } catch (Exception e) {
      log.error("Kafka 메시지 처리 중 오류 발생", e);
      throw e;
    } finally {
      span.end();
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
