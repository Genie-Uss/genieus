package shop.genieus.product.presentation.rest.controller;

import static shop.genieus.product.global.constants.Code.*;

import com.genieus.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import shop.genieus.product.application.in.command.ProductCommandService;
import shop.genieus.product.domain.model.entity.Product;
import shop.genieus.product.global.constants.Code;
import shop.genieus.product.presentation.rest.dto.request.CreateProductRequest;
import shop.genieus.product.presentation.rest.dto.response.CreateProductResponse;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductCommandService productCommandService;

  @PostMapping
  public ResponseEntity<ApiResponse<CreateProductResponse>> createProduct(
      @Valid @RequestBody CreateProductRequest request) {

    Product product = productCommandService.createProduct(request.toCommand());
    URI uri = makeUri(product.getProductId());
    return ResponseEntity.created(uri).body(
        ApiResponse.of(
            CREATE_PRODUCT_SUCCESS.getCode(),
            CREATE_PRODUCT_SUCCESS.getMessage(),
            CreateProductResponse.from(product)
        )
    );
  }

  private URI makeUri(Long promotionId) {
    return ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{promotionId}")
        .buildAndExpand(promotionId)
        .toUri();
  }
}
