package shop.genieus.coupon.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import shop.genieus.coupon.application.in.command.dto.IssueCouponCommand;

@Configuration
public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(host);
    redisStandaloneConfiguration.setPort(port);
    return new LettuceConnectionFactory(redisStandaloneConfiguration);
  }

  @Bean
  public RedisTemplate<String, Long> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

    RedisTemplate<String, Long> RedisTemplate = new RedisTemplate<>();
    RedisTemplate.setConnectionFactory(redisConnectionFactory);
    RedisTemplate.setKeySerializer(new StringRedisSerializer());
    RedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return RedisTemplate;
  }

  @Bean
  public RedisTemplate<String, IssueCouponCommand> CouponredisTemplate(
      RedisConnectionFactory redisConnectionFactory) {

    RedisTemplate<String, IssueCouponCommand> CouponredisTemplate = new RedisTemplate<>();
    CouponredisTemplate.setConnectionFactory(redisConnectionFactory);
    CouponredisTemplate.setKeySerializer(new StringRedisSerializer());
    CouponredisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return CouponredisTemplate;
  }
}
