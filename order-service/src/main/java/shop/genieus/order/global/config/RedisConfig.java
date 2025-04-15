package shop.genieus.order.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

  /**
   * Redis 연결을 위한 LettuceConnectionFactory 빈을 생성합니다.
   *
   * @return 지정된 호스트와 포트로 구성된 RedisConnectionFactory
   */
  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(host, port);
  }

  /**
   * Redis에서 String 타입의 키와 Long 타입의 값을 저장하고 조회할 수 있는 RedisTemplate 빈을 생성합니다.
   *
   * @param factory Redis 연결을 위한 커넥션 팩토리
   * @return String 키와 Long 값을 사용하는 RedisTemplate 인스턴스
   */
  @Bean
  public RedisTemplate<String, Long> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, Long> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericToStringSerializer<>(Long.class));
    template.setEnableTransactionSupport(false);
    return template;
  }
}
