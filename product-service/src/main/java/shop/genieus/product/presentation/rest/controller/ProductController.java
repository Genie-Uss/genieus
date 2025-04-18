package shop.genieus.product.presentation.rest.controller;

import static shop.genieus.product.global.constants.Code.*;

import com.genieus.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import shop.genieus.product.application.in.command.ProductCommandService;
import shop.genieus.product.application.in.command.dto.FindProductCommand;
import shop.genieus.product.application.in.query.ProductQueryService;
import shop.genieus.product.domain.model.ProductView;
import shop.genieus.product.domain.model.entity.Product;
import shop.genieus.product.presentation.rest.dto.request.CreateProductRequest;
import shop.genieus.product.presentation.rest.dto.response.CreateProductResponse;
import shop.genieus.product.presentation.rest.dto.response.FindProductResponse;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductCommandService productCommandService;
  private final ProductQueryService productQueryService;

  @PostMapping
  public ResponseEntity<ApiResponse<CreateProductResponse>> createProduct(
      @Valid @RequestBody CreateProductRequest request) {

    Product product = productCommandService.createProduct(request.toCommand());
    URI uri = makeUri(product.getProductId());
    return ResponseEntity.created(uri)
        .body(
            ApiResponse.of(
                CREATE_PRODUCT_SUCCESS.getCode(),
                CREATE_PRODUCT_SUCCESS.getMessage(),
                CreateProductResponse.from(product)));
  }

  @GetMapping("/{productId}")
  public ResponseEntity<ApiResponse<FindProductResponse>> getProduct(
      @PathVariable("productId") Long productId) {

    ProductView product = productQueryService.getProduct(new FindProductCommand(productId));
    return ResponseEntity.ok()
        .body(
            ApiResponse.of(
                FIND_PRODUCT_SUCCESS.getCode(),
                FIND_PRODUCT_SUCCESS.getMessage(),
                FindProductResponse.from(product)));
  }

  private URI makeUri(Long promotionId) {
    return ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{promotionId}")
        .buildAndExpand(promotionId)
        .toUri();
  }
}
