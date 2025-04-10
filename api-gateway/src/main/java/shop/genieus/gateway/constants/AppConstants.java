package shop.genieus.gateway.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppConstants {
  public static class Trace {
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
  }

  public static class Filter {
    public static final String TOKEN_ATTR = "token";
    public static final String ERROR_MISSING_TOKEN = "토큰이 존재하지 않습니다.";
  }

  public static class ApiPath {
    private static final String INTERNAL_V1 = "/internal/v1";

    public static class Internal {
      public static final String AUTH = INTERNAL_V1 + "/auth";
      public static final String AUTH_ISSUE_PASSPORT = AUTH + "/passport";
    }
  }
}
