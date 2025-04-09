package shop.genieus.promotion.application.in.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shop.genieus.promotion.application.out.persistence.PromotionCommandPort;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionCommandService {

  private final PromotionCommandPort promotionCommandPort;

}
