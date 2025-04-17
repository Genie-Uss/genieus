package shop.genieus.order.infrastructure.event.producer;

import com.genieus.common.event.DomainEvent;
import com.genieus.common.event.EventEnvelope;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaProducer {

  private final KafkaTemplate<String, EventEnvelope<? extends DomainEvent>> kafkaTemplate;
  private final Tracer tracer;

  @Value("${spring.kafka.template.default-topic}")
  private String orderTopic;

  public void publish(String key, EventEnvelope<? extends DomainEvent> eventEnvelop) {
    Span span =
        tracer
            .nextSpan()
            .name("kafka.produce")
            .tag("eventType", eventEnvelop.getEventType())
            .start();

    try (Tracer.SpanInScope scope = tracer.withSpan(span)) {
      TraceContext context = span.context();
      ProducerRecord<String, EventEnvelope<? extends DomainEvent>> record =
          new ProducerRecord<>(orderTopic, key, eventEnvelop);

      record.headers().add(new RecordHeader("traceId", context.traceId().getBytes()));
      record.headers().add(new RecordHeader("spanId", context.spanId().getBytes()));
      if (context.parentId() != null) {
        record.headers().add(new RecordHeader("parentId", context.parentId().getBytes()));
      }

      CompletableFuture<SendResult<String, EventEnvelope<? extends DomainEvent>>> result =
          kafkaTemplate.send(record);

      result.whenComplete(
          (sendResult, exception) -> {
            if (exception != null) {
              log.error("[publish] Kafka 전송 실패: {}", exception.getMessage());
              // TODO: 에러 핸들링 전략 구현 (재시도, DLQ 등)
            } else {
              log.debug(
                  "[publish] Kafka 전송 성공: offset={}", sendResult.getRecordMetadata().offset());
            }
            span.end();
          });
    } catch (Exception e) {
      span.error(e);
      span.end();
      throw e;
    }
  }
}
