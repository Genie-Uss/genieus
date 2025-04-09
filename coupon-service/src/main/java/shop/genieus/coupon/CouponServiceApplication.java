package shop.genieus.coupon;

import com.genieus.common.annotation.EnableCommonLib;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCommonLib
public class CouponServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(CouponServiceApplication.class, args);
  }
}