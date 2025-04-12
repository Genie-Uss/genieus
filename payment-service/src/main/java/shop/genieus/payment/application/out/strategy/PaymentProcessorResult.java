package shop.genieus.payment.application.out.strategy;

public record PaymentProcessorResult(
        ResultType resultType,
        Object payload) {

    public static PaymentProcessorResult returnRedirect(String url) {
        return new PaymentProcessorResult(ResultType.REDIRECT, url);
    }

    public static PaymentProcessorResult returnJson(Object body) {
        return new PaymentProcessorResult(ResultType.JSON, body);
    }

    public enum ResultType {
        REDIRECT,
        JSON
    }
}
