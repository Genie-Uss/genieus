1. tossPay 로 넘어오는 정보 검증 필요
    - orderId 로 payment 객체 한 번 더 조회
        - RequestParam 으로 받은 amount 랑 payment.paymentPrice 랑 같은지 검증
        - paymentMethod 가 TOSS_PAY 인지 검증
2. webhook 붙여야 함
    - 근데 도메인 주소 사용해야 함
        - http, localhost 로 불가능
3. 토스 결제 성공 시 결제 객체 상태 변경해줘야 함 ✅
    - paymentStatus: PENDING -> SUCCESS ✅
    - paymentPaidAt: null -> LocalDateTime.now() ✅
4. ~~결제 객체에 isRefundableisRefundable 추가 하기~~
    - 비즈니스 규칙 적용하기: 결제 완료부터 7일이 지나면 환불 불가
        - ~~isRefundable: false -> true~~ 
    - 근데 이러면 스케줄러 써야 함. 차라리 환불 로직 들어올 때 비즈니스 규칙으로 체크 해주는 게 나음
5. 결제 실패 시 결제 객체 상태 변경 -> FAILED 
6. 결제가 완료되면 비동기 이벤트 쏴야 함
    - payment.success
7. 커스텀 이벤트, 이벤트 핸들러 붙이기
8. 주문 취소 구현
9. 의논
    - dto 네이밍 규칙은 확인 ProcessPaymentCommand
    - 메서드는? process()? processPayment()?