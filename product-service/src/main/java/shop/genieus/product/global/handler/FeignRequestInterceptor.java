package shop.genieus.product.global.handler;

import static com.genieus.common.auth.constant.PassportConstant.PASSPORT_HEADER;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeignRequestInterceptor implements RequestInterceptor {
  @Override
  public void apply(RequestTemplate template) {

    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      String passportHeader = attributes.getRequest().getHeader(PASSPORT_HEADER);

      if (passportHeader != null) {
        template.header(PASSPORT_HEADER, passportHeader);
      }
    }
  }
}
