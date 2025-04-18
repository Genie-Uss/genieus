package shop.genieus.promotion.presentation.event.consumer;

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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductKafkaConsumer {

    private static final String SPAN_NAME = "kafka.consume";

    private final Tracer tracer;
    private final EventRouter eventRouter;

    @Transactional
    @KafkaListener(topics = "product-events", containerFactory = "kafkaListenerContainerFactory")
    public void consume(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(value = "traceId", required = false) String traceId,
            @Header(value = "spanId", required = false) String spanId) {

        Span consumerSpan = createConsumerSpan(traceId, spanId);
        try (Tracer.SpanInScope scope = tracer.withSpan(consumerSpan)) {
            eventRouter.route(topic, payload);
            log.info("이벤트 라우팅 성공, topic={}, payload={}", topic, payload);
        } catch (Exception e) {
            log.error("이벤트 라우팅 에러, topic: {}, payload: {}, error:{}", topic, payload, e.getMessage());
        } finally {
            consumerSpan.end();
        }
    }

    private Span createConsumerSpan(String traceId, String spanId) {
        if (traceId == null || spanId == null) {
            return tracer.nextSpan().name(SPAN_NAME).start();
        }
        TraceContext parentContext =
                tracer.traceContextBuilder().traceId(traceId).
                        spanId(spanId).sampled(true).build();
        return tracer.spanBuilder().name(SPAN_NAME).setParent(parentContext).start();
    }
}
