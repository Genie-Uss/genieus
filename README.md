# 지니어스, 소원을 이뤄주는 최저가 쇼핑

![image](https://github.com/user-attachments/assets/967fd02f-2e07-4224-a194-1b0ace303b28)

## 서비스/프로젝트 소개
**지니어스(Genie-Us)는 우리 곁의 천재(Genius)가 소원을 이뤄주듯, 최저가 상품과 선착순 쿠폰으로 최고의 쇼핑 경험을 제공합니다.** </br>
Genie(🧞‍♂️)와 Us(우리)가 만나, 가장 합리적인 소비를 완성합니다.

> "🪔 최저가를 소환하세요, 지니어스와 함께!”
</br>
</aside>

## 서비스/프로젝트 목표

**1️⃣ MSA 멀티 프로젝트 구조를 통한 확장성 확보**

- 각 기능을 독립된 서비스로 분리하여 대규모 트래픽 상황에서도 안정적이고 유연한 서비스를 운영합니다.

**2️⃣ Feign과 Kafka를 조합한 안정적인 통신 방식**

- 핵심 트랜잭션은 Feign 동기 통신으로 처리하고, 실패 복구와 후속 처리는 Kafka 비동기 이벤트로 안정성을 강화했습니다.

**3️⃣ Batch 처리를 통한 안정적인 대량 데이터 저장**

- 임시 저장된 **선착순 쿠폰 발급 데이터**와 **재고 히스토리 데이터**를 **Spring Batch**로 주기적 수집 및 **Bulk Insert**하여 대량 데이터 입력 시 안정성과 일관성을 확보하였습니다.

**4️⃣ Redis를 활용한 실시간 프로모션 최저가 보장**

- 프로모션은 상품 별 할인율을 관리하고, Redis에 구간별 상품의 최대 할인율을 사전에 저장해 실시간으로 정확한 최저가 제공이 가능합니다.
- 프로모션 관리 및 할인율 조회 성능을 최적화하여 사용자가 언제나 최적의 가격 혜택을 누릴 수 있도록 보장합니다.

 **5️⃣ Redis를 활용한 안정적 재고 관리**

- **Lua 스크립트를 활용한 원자적 재고 처리**로 주문 발생 시 **상품 재고**와 선착순 **쿠폰 재고**를 정확하고 안전하게 관리합니다.
- **Redis 기반의 빠른 동기식 검증**으로 대규모 트래픽 상황에서도 실시간으로 안정적인 재고 관리가 가능합니다.

 **6️⃣ Prometheus, Grafana, Loki, k6를 활용한 통합 모니터링 및 성능 테스트**

- 시스템 지표와 로그를 통합 관리하여 장애를 조기에 감지하고 실시간 대응할 수 있는 모니터링 체계를 구축했습니다.
- 실제 사용자 흐름을 기반으로 부하 테스트를 수행하고, 시스템의 병목 지점을 사전에 개선했습니다.

</br>

## 인프라 설계

<aside>

### 시스템 아키텍처 설계도

![인프라_아키텍처 drawio](https://github.com/user-attachments/assets/7d6074dd-b73d-436c-9f2e-9845e2373b56)

### 카프카 이벤트 흐름
![image 2](https://github.com/user-attachments/assets/c74ba7d2-7dd8-4c10-b61d-a07249715572)

</aside>

## 주요 기능

### [🌐 게이트웨이 & 인증]()

### [🪄 주문]()

### [💸 결제]()

### [🔥프로모션]()

### [🎁 상품]()

### [🎟️ 쿠폰]()

</br>

## 적용 기술

 **📁 프레임워크 / 라이브러리**

[JDK 17.0.13](https://www.notion.so/JDK-17-0-13-1ed94064be0581fa8ceedad22459c610?pvs=21)

[SpringBoot 3.4.4](https://www.notion.so/SpringBoot-3-4-4-1ed94064be0581b8b372ea78ed4b3a6e?pvs=21)

[Eureka](https://www.notion.so/Eureka-1ed94064be05814e9029e9f3f7354c08?pvs=21)

[Spring Cloud Gateway](https://www.notion.so/Spring-Cloud-Gateway-1ed94064be058121bcf9dfcd0559c376?pvs=21)

[Spring Data JPA](https://www.notion.so/Spring-Data-JPA-1ed94064be0581b0b045f06437eea44e?pvs=21)

[OpenFeign](https://www.notion.so/OpenFeign-1ed94064be05818694ead0f7ef15fd30?pvs=21)

[R`esilience4j`](https://www.notion.so/Resilience4j-1ed94064be05811a9d47d074d9bdb780?pvs=21)

[Spring Batch](https://www.notion.so/Spring-Batch-1ed94064be05818d9240d2e04767a18c?pvs=21)

[Jenkins](https://www.notion.so/Jenkins-1ed94064be0581409ec9e1733c6f386b?pvs=21)

 **🗄️ 데이터베이스**

[MySQL 8.0](https://www.notion.so/MySQL-8-0-1ed94064be0581619d20feb34e3de6a8?pvs=21)

[Redis](https://www.notion.so/Redis-1ed94064be058135be50d7a522348f5d?pvs=21)

 **📮 이벤트**

[Apache Kafka](https://www.notion.so/Apache-Kafka-1ed94064be058194b782d4b0280bbe98?pvs=21)

 **📈 모니터링 / 로그**

[Grafana](https://www.notion.so/Grafana-1ed94064be0581f99f68d73dda7d42c4?pvs=21)

[Prometheus](https://www.notion.so/Prometheus-1ed94064be0581278c42c571e8a376f1?pvs=21)

[Loki](https://www.notion.so/Loki-1ed94064be0581f69945f3e34baa8035?pvs=21)

[Promtail](https://www.notion.so/Promtail-1ed94064be0581ec92f8eeb842ecb40e?pvs=21)

[Zipkin](https://www.notion.so/Zipkin-1ed94064be0581d59083c0f683009938?pvs=21)

[K6](https://www.notion.so/K6-1ed94064be0581a6a9b6c5e5801572b7?pvs=21)

 **🌐 인프라**

[Nginx](https://www.notion.so/Nginx-1ed94064be05817ab036c21334b85010?pvs=21)

[Docker](https://www.notion.so/Docker-1ed94064be05814eb14dfd9c67f29878?pvs=21)

[GitHub Actions](https://www.notion.so/GitHub-Actions-1ed94064be05815d95b3ea28ed3377f3?pvs=21)

[AWS EC2](https://www.notion.so/AWS-EC2-1ed94064be058117abd1c77657609463?pvs=21)

[AWS ECR](https://www.notion.so/AWS-ECR-1ed94064be058165b39fe52a57aa2d4b?pvs=21)

[AWS RDS](https://www.notion.so/AWS-RDS-1ed94064be05816c9e1de5a6aba95f17?pvs=21)

[AWS VPC](https://www.notion.so/AWS-VPC-1ed94064be058133a14df1f01f143222?pvs=21)

[AWS Route53](https://www.notion.so/AWS-Route53-1ed94064be0581418b94fc03a1a3cab1?pvs=21)

 **⌨️ 협업툴**

[Github](https://www.notion.so/Github-1ed94064be0581c5a335e4fcf5cd68d9?pvs=21)

[Slack](https://www.notion.so/Slack-1ed94064be0581ca8dafc4531436dac2?pvs=21)

[Notion](https://www.notion.so/Notion-1ed94064be058164a56bf4c08a086809?pvs=21)

[Discord](https://www.notion.so/Discord-1ed94064be05814786b7fff6d23a5f6f?pvs=21)
</br>

## 팀 컨벤션

Naming Convention
- 계층별 네이밍
- MSA 동기 통신시 네이밍 규칙

Branch Convention

PR Convention

Commit Convention

Package Convention

Issue Convention

## 기술적 의사결정

공통 라이브러리 설계 및 의존성 관리 전략

서비스 간 통신 방식 결정 (동기 vs 비동기)

성능 최적화를 위한 배치 및 캐시 전략

통합 모니터링 및 트레이싱 환경 구축

서비스 장애 대응 및 복구 전략 수립

배포 전략 결정 및 클라우드 인프라 설계

## 트러블슈팅

MSA 환경에서 API Gateway 연결 트러블슈팅

RDS 커넥션 제한과 Hikari CP 충돌 문제

실시간 최저가 상품 반영을 위한 엔티티 구조 설계

배포 서버 배치 실행 시 직렬화 문제

서킷브레이커 사용시 예외 문제

부하테스트를 통한 성능 개선

## CONTRIBUTORS

| 팀원명 | 포지션 | 담당(개인별 기여점) | 깃허브 링크 |
| --- | --- | --- | --- |
| 정우준 | 👑 Team-Leader | ▶ 배포 전략 수립</br>- 별도 인프라 구축 없는 Github Actions를 빌드 서버로 채택</br>- 배포 단위를 경량화 하고, 태그로써 롤백 관리를 하기 위해 ECR 선택(규모를 고려하여 CodeDeploy 생략)</br>▶ 클라우드 인프라 담당</br>- VPC 내 퍼블릭/프라이빗 서브넷을 구축, 각각 라우팅 테이블을 설정하여 Internet/NAT Gateway에 연결함으로써 외부 진입과 내부 아웃바운드를 분리</br>- Bastion 호스트를 통한 SSH 단일 접근점 구성 및 Route 53으로 사용자 도메인 연결</br>- 최소 권한 원칙 적용한 보안 그룹 설정 및 팀원, 서비스별 IAM 역할 및 정책을 관리함</br>- Kafka, ZooKeeper, Redis 서버와 MSA 서버를 프라이빗 서브넷에 분리하여 배치하고 보안 그룹 및 네트워크 ACL로 제어</br>- 두 개의 가용 영역에 서브넷을 분리하고 RDS를 배포함으로써 데이터 복제 및 고가용성 확보, 필요 시 무중단 스케일 업 수행</br>▶ 결제 서비스 설계 및 개발</br>- 전략 패턴과 팩토리 패턴을 결합하여 결제 수단 확장성 보장 및 유연한 결제 처리 구조 구현</br>- Toss Payment API 연동 및 단일 엔드포인트에서 RestAPI/뷰 랜더링 응답을 유연하게 처리</br>- Redis 캐싱 전략으로 DB 부하 감소 및 결제 처리 속도 최적화</br>- 아웃박스 패턴을 도입하여 결제 완료와 이벤트 발행을 트랜잭션으로 묶어 데이터 정합성 보장</br>- 스케줄러 기반 polling 이벤트 발행으로 복잡한 CDC 인프라 없이 안정적인 메시지 전달 구현 | https://github.com/orkrj |
| 최성민 | 👑 Tech-Leader | **▶** 공통 라이브러리 설계 및 구축</br>- 이벤트 타입 매핑과 역직렬화 문제를 해결하는 공통 이벤트 처리 모듈을 개발하여, 서비스 간 일관된 이벤트 통신 체계를 구축</br>- 공통 모듈 버전 관리를 통해 서비스 간 충돌을 방지하고, 팀원들이 비즈니스 로직에 집중할 수 있는 환경을 제공</br>**▶** 주문서비스 핵심 로직 설계 및 구현 </br>**-** 주문 생성 과정에서 FeignClient 기반의 동기 호출에 Resilience4j(서킷브레이커 + 리트라이)를 적용하여 장애 복원력 강화</br>**-** 재고 예약, 쿠폰 사용 등 즉각적인 처리가 필요한 흐름은 동기로, 재고 복구 및 쿠폰 복구는 Kafka 이벤트 기반 비동기로 분리하여 트래픽 부하 최소화</br>- Redis Sorted Set을 활용한 Delay Queue를 기반으로 주문 만료를 관리하고, 스케줄러를 통해 만료된 주문을 일괄로 배치 처리하여 성능 최적화</br>**▶** 모니터링 및 로깅 시스템 구축</br>- Grafana, Prometheus, Loki, Promtail, Zipkin을 기반으로 분산 추적 및 통합 모니터링 대시보드를 구축</br>- Slack Webhook 알림을 연동하여 장애 발생 시 신속하게 감지하고 대응할 수 있는 체계 마련</br>- 통일된 로깅 체계를 위해 Logging Aspect를 적용하여, 모든 API 진입점의 요청/응답 및 처리 시간을 자동 수집</br>**▶** 부하테스트 및 성능 개선</br>- k6와 Grafana를 활용하여 다양한 부하 시나리오를 설계하고 실행, 실제 운영 환경과 유사한 조건에서 테스트 진행</br>- CPU, 메모리 자원 소모를 기반으로 시스템의 최대 처리 가능 요청 수를 측정하고, 향후 성능 튜닝 방향성을 도출</br>- 테스트를 통해 병목 지점을 사전 파악하고, 안정적인 트래픽 수용 능력을 확보하기 위한 기초 데이터를 마련</br>**▶** CI/CD 자동화 구축</br>- GitHub Actions 기반으로 변경된 서비스만 자동 빌드 및 배포하도록 워크플로우를 구성하여, 개발 및 운영 효율성 향상</br>- 긴급 상황에 대비하여 수동 트리거 기반의 빠른 배포 플로우도 추가 지원 | https://github.com/seongmin1117 |
| 김수빈 | 🫅 Member | **▶ 프로젝트 초기 세팅**</br>- 각 마이크로서비스에 대한 빌드 스크립트 및 설정 파일 작성</br>- 로컬 개발 환경 표준화를 위해 Docker Compose 기반의 DB 및 인프라 구성</br>- 유레카 서버 설정 및 서비스 등록/연결 구조 구성</br>**▶ API Gateway 설계**</br>- 인증/인가, 로깅 등 글로벌 필터 설계 및 적용</br>- 서비스별 경로 기반 라우팅 및 매핑 규칙 구성</br>- Sleuth 기반 분산 추적 필터 적용 및 리액티브 컨텍스트 전파 구조 설계</br>**▶ 인증 서버 개발**</br>- JWT 기반의 Stateless 인증 방식 구현</br>- 게이트웨이 연동을 위한 내부 인증 API 설계 및 패스포트 발급 기능 개발</br>**▶ 각 서비스 인증·인가를 위한 공통 라이브러리 개발**</br>- 인증객체 주입, 권한 체크 등의 공통 로직을 모듈화하여 재사용 가능한 라이브러리 형태로 구현</br>- AOP, 필터 체인, Argument Resolver, 어노테이션 기반으로 인증·인가 로직이 동작하도록 추상화</br>**▶ 유저 도메인 개발**</br>- 회원가입 기능 및 사용자 서비스와 인증 서비스 간의 동기화 기능을 구현</br>**▶ 상품 도메인 개발**</br>- 상품 조회 및 정보, 재고 정보 캐싱, 상품 생성 후 이벤트 발행</br>- Lua 스크립트를 활용한 전체 상품 재고 사전 검증 및 일괄 차감 로직 구현 (원자적 실행)</br>- 재고 관련 이벤트를 Redis Sorted Set에 저장하고, Spring Scheduler와 Spring Batch를 사용해 일정 단위로 재고 히스토리에 Bulk Insert하는 배치 처리 구현</br>**▶ 운영 서버**</br>퍼블릭 서브넷에 Nginx 기반 리버스 프록시 서버 구축으로 다음 기능 통합:</br>- SSL 터미네이션(HTTPS 보안 연결)</br>- 사용자 요청을 적절한 내부 서비스로 자동 라우팅하는 환경을 구축</br>- 모니터링 서비스(Eureka, Zipkin, Grafana, Prometheus, Kafka UI 등)에 대한 인증을 설정하여 보안 환경 구축</br>**DNS 검증을 통한 인증서 자동 갱신 시스템 구성으로 보안 인프라 유지보수 간소화:**</br>- AWS Route 53으로 도메인 및 서브도메인 DNS 관리 연동</br>- AWS Route 53과 Let's Encrypt를 활용한 와일드카드 SSL 인증서 발급 및 만료 갱신 자동화 구현 | https://github.com/Soobinnni |
| 류지윤 | 🫅 Member | **▶ 쿠폰 서비스 구현**</br>- 안정적인 선착순 쿠폰 발급을 위한 도메인 구조 설계 및 구현</br>- Lua Script를 활용하여 Redis 에서 쿠폰 재고 차감 및 발급 정보 저장을 원자적으로 처리하여 중복 발급 방지</br>- 발급된 쿠폰 목록을 Spring Batch로 주기적으로 읽어 MySQL에 대량 저장하는 배치 프로세스 개발</br>- 불규칙한 발급 시작 시점을 대응하기 위해 Jenkins Shell Script 기반으로 외부에서 배치 실행 시점을 유연하게 제어하는 시스템 구축</br>- 발급 실패 등 예외 상황에 대비해 실패 데이터를 Redis에 재저장하여 데이터 유실을 방지하고 처리 안정성 확보</br>- Kafka 이벤트 기반으로 주문 취소 시 쿠폰 취소 이벤트를 수신하는 기능 구현</br>- 이벤트 수신 후 도메인 서비스 레이어에서 쿠폰 사용 가능 여부(만료일, 상태 등) 검증 및 사용 처리 로직 설계 | https://github.com/Ryujy |
| 박상욱 | 🫅 Member | **▶ 프로모션 서비스 구현**</br>- 동일 상품이 여러 프로모션에 중복 등록되는 구조를 고려하여, 재고 관리는 상품 도메인에서 일관되게 처리하고 프로모션 도메인에서는 할인 정책 중심으로 도메인 설계</br>- 구간 단위로 상품별 최대 할인율을 미리 계산하여 Redis에 저장. TTL을 구간 만료 시점으로 설정해 자동 관리</br>- 실시간 최대 할인율 조회를 위해 Redis의 MultiGet을 활용해 빠르게 일괄 조회하고, 값이 하나라도 누락된 경우 예외 처리</br>- Spring Scheduler를 통해 다음 구간의 최대 할인 데이터를 미리 계산해 Redis에 반영하도록 구현</br>- 예상치 못한 장애나 데이터 누락 상황에서는 Cache-Aside 전략을 통해 DB에서 직접 조회 후 Redis에 보완 처리</br>- 구간 데이터 캐시 미스 시 캐시 쏠림(Cache Stampede)을 방지하기 위해 Redisson 분산 락과 이중 확인(Double-Check) 패턴을 적용</br>- 상품 생성 이벤트를 Kafka로 수신해 상시판매 프로모션에 저장 후 이벤트 발행 → 이벤트 리스너가 트랜잭션 커밋 이후 Redis에 해당 상품의 정보를 반영하는 비동기 이벤트 처리 구현</br>**▶ 상품 서비스 구현**</br>- Redis Lua Script를 활용해 총재고 및 사용량 사전 검증 및 원자적 일괄 차감 로직 구현</br>- 주문 완료/취소 이벤트를 Kafka로 컨슘했을때, 상품별 고유 EventId를 생성하여 Redis Hash와 Sorted Set으로 재고 이벤트 관리</br>- Spring Scheduler 및 Spring Batch를 활용해 일정량의 재고 이벤트를 가져와 Bulk Insert하는 배치 처리 구현</br>- 중복 주문 이벤트 처리 방지를 위해 Redis SET를 활용해 중복 검증 구현 | https://github.com/sosa7753 |
