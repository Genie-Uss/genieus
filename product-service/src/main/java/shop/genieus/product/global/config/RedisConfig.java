package shop.genieus.product.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import shop.genieus.product.domain.model.ProductView;

@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<String, ProductView> productViewRedisTemplate(
      RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, ProductView> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    template.setKeySerializer(new StringRedisSerializer());

    template.setValueSerializer(new Jackson2JsonRedisSerializer<>(ProductView.class));

    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(ProductView.class));

    template.afterPropertiesSet();
    return template;
  }

  @Bean
  public RedisTemplate<String, Long> longRedisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Long> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    template.setKeySerializer(new StringRedisSerializer());

    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

    template.afterPropertiesSet();
    return template;
  }
}
