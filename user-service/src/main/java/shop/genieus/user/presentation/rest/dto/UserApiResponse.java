package shop.genieus.user.presentation.rest.dto;

import com.genieus.common.response.ApiResponse;

public record UserApiResponse<T>(String message, T data) {
  private static final int USER_SERVICE_CODE = 1000;
  private static final String DEFAULT_SUCCESS_MESSAGE = "응답이 성공적으로 완료되었습니다.";

  public static <T> ApiResponse<T> of(String message, T data) {
    return ApiResponse.of(USER_SERVICE_CODE, message, data);
  }

  public static <T> ApiResponse<T> ok(T data) {
    return ApiResponse.of(USER_SERVICE_CODE, DEFAULT_SUCCESS_MESSAGE, data);
  }
}
