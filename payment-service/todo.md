1. ✅tossPay 로 넘어오는 정보 검증 필요
    - orderId 로 payment 객체 한 번 더 조회
        - RequestParam 으로 받은 amount 랑 payment.paymentPrice 랑 같은지 검증
        - paymentMethod 가 TOSS_PAY 인지 검증
2. ⚠️webhook 붙여야 함
    - 근데 도메인 주소 사용해야 함
        - http, localhost 로 불가능
3. ✅토스 결제 성공 시 결제 객체 상태 변경해줘야 함
    - paymentStatus: PENDING -> SUCCESS
    - paymentPaidAt: null -> LocalDateTime.now()
4. ~~결제 객체에 isRefundableisRefundable 추가 하기~~
   ✅- 비즈니스 규칙 적용하기: 결제 완료부터 7일이 지나면 환불 불가
    - ~~isRefundable: false -> true~~
    - 근데 이러면 스케줄러 써야 함. 차라리 환불 로직 들어올 때 비즈니스 규칙으로 체크 해주는 게 나음
5. ⚠️**결제 실패 시 결제 객체 상태 변경 -> FAILED**
    - 결제 실패 시 fail.html 이 랜더링되는데, code 랑 메세지만 줌..
        - 그래서 orderId 를 checkout.html url 에 쿼리 파티미터로 넣으면 토스에서 허용되지 않는 요청으로 전부 차단함
        - 웹훅을 사용하면 orderId 를 성공 실패 상관 없이 받을 수 있음
        - 프론트가 있는 경우 쿠키에 orderId 를 저장할 수 있는데 우리는 프론트가 없고, 토스 페이도 리다이렉트로 연동시켜주고 있어서 방법이 없음!
6. ✅결제가 완료되면 비동기 이벤트 쏴야 함
    - payment-events
7. ✅커스텀 이벤트, 이벤트 핸들러 붙이기
8. ✅주문 취소 이벤트를 소비하여 결제 취소 구현
9. api-gateway-route

```yaml

- id: payment-list
  uri: lb://PAYMENT-SERVICE
  predicates:
    - Path=/api/v1/payments
    - Method=GET
  filters:
    - TokenExtractor
    - Authentication

- id: payment-detail
  uri: lb://PAYMENT-SERVICE
  predicates:
    - Path=/api/v1/payments/{paymentId}
    - Method=GET
  filters:
    - TokenExtractor
    - Authentication

- id: payment-create
  uri: lb://PAYMENT-SERVICE
  predicates:
    - Path=/api/v1/payments
    - Method=POST
  filters:
    - TokenExtractor
    - Authentication

- id: payment-process
  uri: lb://PAYMENT-SERVICE
  predicates:
    - Path=/api/v1/payments/process
    - Method=POST
  filters:
    - TokenExtractor
    - Authentication

- id: payment-success
  uri: lb://PAYMENT-SERVICE
  predicates:
    - Path=/api/v1/payments/success
    - Method=POST
  filters:
    - TokenExtractor
    - Authentication

- id: payment-fail
  uri: lb://PAYMENT-SERVICE
  predicates:
    - Path=/api/v1/payments/fail
    - Method=POST
  filters:
    - TokenExtractor
    - Authentication

- id: payment-refund
  uri: lb://PAYMENT-SERVICE
  predicates:
    - Path=/api/v1/payments/{paymentId}/refund
    - Method=POST
  filters:
    - TokenExtractor
    - Authentication

- id: payment-delete
  uri: lb://PAYMENT-SERVICE
  predicates:
    - Path=/api/v1/payments/{paymentId}
    - Method=PATCH
  filters:
    - TokenExtractor
    - Authentication

- id: toss-checkout
  uri: lb://PAYMENT-SERVICE
  predicates:
    - Path=/api/v1/payments/toss
    - Method=GET
  filters:
    - TokenExtractor
    - Authentication

- id: toss-confirm
  uri: lb://PAYMENT-SERVICE
  predicates:
    - Path=/api/v1/payments/toss/confirm
    - Method=POST
  filters:
    - TokenExtractor
    - Authentication

- id: toss-success
  uri: lb://PAYMENT-SERVICE
  predicates:
    - Path=/api/v1/payments/toss/success
    - Method=GET
  filters:
    - TokenExtractor
    - Authentication

- id: toss-fail
  uri: lb://PAYMENT-SERVICE
  predicates:
    - Path=/api/v1/payments/toss/fail
    - Method=GET
  filters:
    - TokenExtractor
    - Authentication

```