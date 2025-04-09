package shop.genieus.promotion.global.common;

public record ApiResponse<T>(
    Integer code,
    String message,
    T data
) {

  public static <T> ApiResponse<T> fail(Integer code, String message) {
    return new ApiResponse<>(code, message, null);
  }

  public static <T> ApiResponse<T> of(Integer code, String message, T data) {
    return new ApiResponse<>(code, message, data);
  }
}