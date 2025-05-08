# 지니어스, 소원을 이뤄주는 최저가 쇼핑

<img src="https://github.com/user-attachments/assets/967fd02f-2e07-4224-a194-1b0ace303b28" width="700"/>

## 서비스/프로젝트 소개

**지니어스(Genie-Us)는 우리 곁의 천재(Genius)가 소원을 이뤄주듯, 최저가 상품과 선착순 쿠폰으로 최고의 쇼핑 경험을 제공합니다.** </br>
Genie(🧞‍♂️)와 Us(우리)가 만나, 가장 합리적인 소비를 완성합니다.

> "🪔 최저가를 소환하세요, 지니어스와 함께!”

<br/>

## 개발 인원 및 역할

| <div align="center">[<img src="https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png" width="20">](https://github.com/orkrj) 정우준</div> | <div align="center">[<img src="https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png" width="20">](https://github.com/seongmin1117) 최성민</div> | <div align="center">[<img src="https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png" width="20">](https://github.com/Soobinnni) 김수빈</div> | <div align="center">[<img src="https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png" width="20">](https://github.com/Ryujy) 류지윤</div> | <div align="center">[<img src="https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png" width="20">](https://github.com/sosa7753) 박상욱</div> |
|:---------------------------------------------------------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------|:-------------------------------------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------|
| [![정우준](https://github.com/orkrj.png)](https://github.com/orkrj)                                                                                             | [![최성민](https://github.com/seongmin1117.png)](https://github.com/seongmin1117)                                                                                             | [![김수빈](https://github.com/Soobinnni.png)](https://github.com/Soobinnni)                                                                                             | [![류지윤](https://github.com/Ryujy.png)](https://github.com/Ryujy)                                                                                             | [![박상욱](https://github.com/sosa7753.png)](https://github.com/sosa7753)                                                                                             |
| <div align="center">[📝 Docs](https://github.com/Genie-Uss/genieus/wiki/정우준)</div>                                               | <div align="center">[📝 Docs](https://github.com/Genie-Uss/genieus/wiki/최성민)</div>                                                      | <div align="center">[📝 Docs](https://github.com/Genie-Uss/genieus/wiki/김수빈)</div>                                                   | <div align="center">[📝 Docs](https://github.com/Genie-Uss/genieus/wiki/류지윤)</div>                                               | <div align="center">[📝 Docs](https://github.com/Genie-Uss/genieus/wiki/박상욱)</div>                                                  |
| • 결제 도메인 개발<br/>• 배포 전략 수립<br/>• 클라우드 인프라                                                                                      | • 주문 도메인 개발<br/>• 모니터링 / 로깅  <br/>• 공통 라이브러리 설계                                                                                   | • 유저 도메인 개발<br/>• 상품 도메인 개발<br/>• 게이트웨이 및 인증 <br/>• 공통 인증 모듈 설계                                                                                     | • 쿠폰 도메인 개발                                                                                                                 | • 상품 도메인 개발<br/>• 프로모션 도메인 개발                                                                                                      |

</br>

## 서비스/프로젝트 목표

**1️⃣ MSA 멀티 프로젝트 구조를 통한 확장성 확보**

- 각 기능을 독립된 서비스로 분리하여 대규모 트래픽 상황에서도 안정적이고 유연한 서비스를 운영합니다.

**2️⃣ Feign과 Kafka를 조합한 안정적인 통신 방식**

- 핵심 트랜잭션은 Feign 동기 통신으로 처리하고, 실패 복구와 후속 처리는 Kafka 비동기 이벤트로 안정성을 강화했습니다.

**3️⃣ Batch 처리를 통한 안정적인 대량 데이터 저장**

- 임시 저장된 **선착순 쿠폰 발급 데이터**와 **재고 히스토리 데이터**를 **Spring Batch**로 주기적 수집 및 **Bulk Insert**하여 대량 데이터 입력 시 안정성과 일관성을 확보하였습니다.

**4️⃣ Redis를 활용한 실시간 프로모션 최저가 보장**

- 프로모션은 상품 별 할인율을 관리하고, Redis에 구간별 상품의 최대 할인율을 사전에 저장해 실시간으로 정확한 최저가 제공이 가능합니다.
- 프로모션 관리 및 할인율 조회 성능을 최적화하여 사용자가 언제나 최적의 가격 혜택을 누릴 수 있도록 보장합니다.

**5️⃣ Redis를 활용한 안정적 재고 관리**

- **Lua 스크립트를 활용한 원자적 재고 처리**로 주문 발생 시 **상품 재고**와 선착순 **쿠폰 재고**를 정확하고 안전하게 관리합니다.
- **Redis 기반의 빠른 동기식 검증**으로 대규모 트래픽 상황에서도 실시간으로 안정적인 재고 관리가 가능합니다.

**6️⃣ Prometheus, Grafana, Loki, k6를 활용한 통합 모니터링 및 성능 테스트**

- 시스템 지표와 로그를 통합 관리하여 장애를 조기에 감지하고 실시간 대응할 수 있는 모니터링 체계를 구축했습니다.
- 실제 사용자 흐름을 기반으로 부하 테스트를 수행하고, 시스템의 병목 지점을 사전에 개선했습니다.

</br>

## 인프라 설계

### 시스템 아키텍처 설계도

<img src="https://github.com/user-attachments/assets/7d6074dd-b73d-436c-9f2e-9845e2373b56" width="800"/>

### 카프카 이벤트 흐름

<img src="https://github.com/user-attachments/assets/c74ba7d2-7dd8-4c10-b61d-a07249715572" width="800"/>

## 주요 기능

### 🌐 게이트웨이 & 인증
- 토큰 기반 인증 시스템 구현 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/토큰-기반-인증-시스템-구현)
- 패스포트를 발급하여 각 마이크로 서비스 간 인증 객체로 활용 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/패스포트를-발급하여-각-마이크로-서비스-간-인증-객체로-활용)
- 비동기 환경에서의 분산 추적: 게이트웨이로부터 시작되는 컨텍스트 전파 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/비동기-환경에서의-분산-추적-게이트웨이로부터-시작되는-컨텍스트-전파)

### 🪄 주문
- Resilience4j CircuitBreaker + Retry 조합으로 외부 장애 발생 시 fallback 처리 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/Feign-장애-대응-구조-및-예외-흐름-문제-해결)
- 재고 예약은 동기 호출(Feign)로 처리하고, 재고 차감 및 복구는 비동기 이벤트(Kafka)로 분리 설계 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/주문-서비스-핵심-로직-설계-및-트래픽-대응-구조)
- Redis Sorted Set 기반의 Delay Queue와 배치 업데이트를 활용하여 주문 만료 처리 성능 최적화 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/주문-서비스-핵심-로직-설계-및-트래픽-대응-구조)

### 💸 결제
- 결제 수단의 확장과 축소를 고려하여 전략 패턴과 팩토리 패턴 사용 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/결제-수단의-확장과-축소를-고려하여-전략-패턴과-팩토리-패턴-사용)
- 성능 최적화를 위해 객체 생성 시 캐싱, 결제 완료 시 이벤트 처리함 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/성능-최적화를-위해-객체-생성-시-캐싱,-결제-완료-시-이벤트-처리함)
- 아웃박스 패턴을 사용하여 결제 완료와 이벤트 발행의 정합성 보장 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/아웃박스-패턴을-사용하여-결제-완료와-이벤트-발행의-정합성-보장)

### 🔥 프로모션
- 실시간으로 상품의 최저가 조회 보장 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/실시간으로-상품의-최저가-조회-보장)
- 상품이 추가됐을 때, Redis에 해당 상품 반영 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/상품이-추가됐을-때,-Redis에-해당-상품-반영)

### 🎁 상품
- 주문 요청시 상품들의 재고 검증 및 예약 기능 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/주문-요청시-상품들의-재고-검증-및-예약-기능)
- 주문 완료/취소/만료 이벤트에 대한 재고 복구/차감 처리 기능 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/주문-완료/취소/만료-이벤트에-대한-재고-복구/차감-처리-기능)
- 재고 변경 히스토리 배치 처리 기능 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/재고-변경-히스토리-배치-처리-기능)

### 🎟️ 쿠폰
- Redis 기반의 선착순 쿠폰 발급 처리로 정확한 재고 관리 및 중복 발급 방지 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/Redis-기반의-선착순-쿠폰-발급-처리로-정확한-재고-관리-및-중복-발급-방지)
- Spring Batch + Jenkins기반 대용량 쿠폰 발급 이력 저장 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/Spring-Batch-+-Jenkins기반-대용량-쿠폰-발급-이력-저장)

</br>

## 적용 기술

### 📁 프레임워크 / 라이브러리

![JDK 17](https://img.shields.io/badge/JDK%2017-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?logo=spring&logoColor=white)  
![Eureka](https://img.shields.io/badge/Eureka-6DB33F?logo=spring&logoColor=white)
![Spring Cloud Gateway](https://img.shields.io/badge/Spring%20Cloud%20Gateway-6DB33F?logo=spring&logoColor=white)
![OpenFeign](https://img.shields.io/badge/OpenFeign-6DB33F?logo=spring&logoColor=white)  
![Resilience4j](https://img.shields.io/badge/Resilience4j-4A90E2?logo=resilience4j&logoColor=white)
![Spring Batch](https://img.shields.io/badge/Spring%20Batch-6DB33F?logo=spring&logoColor=white)
![Jenkins](https://img.shields.io/badge/Jenkins-D24939?logo=jenkins&logoColor=white)

### 🗄️ 데이터베이스

![MySQL](https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white)

### 📮 이벤트

![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-231F20?logo=apachekafka&logoColor=white)

### 📈 모니터링 / 로그

![Grafana](https://img.shields.io/badge/Grafana-F46800?logo=grafana&logoColor=white)
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?logo=prometheus&logoColor=white)
![Zipkin](https://img.shields.io/badge/Zipkin-DE0D92?logo=zipkin&logoColor=white)  
![Loki](https://img.shields.io/badge/Loki-0C2233?logo=grafana&logoColor=white)
![Promtail](https://img.shields.io/badge/Promtail-FF5C00?logo=grafana&logoColor=white)
![K6](https://img.shields.io/badge/K6-7B42BC?logo=k6&logoColor=white)

### 🌐 인프라

![Nginx](https://img.shields.io/badge/Nginx-009639?logo=nginx&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?logo=githubactions&logoColor=white)  
![AWS RDS](https://img.shields.io/badge/AWS%20RDS-527FFF?logo=amazonaws&logoColor=white)
![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?logo=amazonaws&logoColor=white)
![AWS ECR](https://img.shields.io/badge/AWS%20ECR-FF9900?logo=amazonaws&logoColor=white)
![AWS VPC](https://img.shields.io/badge/AWS%20VPC-FF9900?logo=amazonaws&logoColor=white)
![AWS Route53](https://img.shields.io/badge/AWS%20Route53-FF9900?logo=amazonaws&logoColor=white)

### 🤝 협업툴

![GitHub](https://img.shields.io/badge/GitHub-181717?logo=github&logoColor=white)
![Slack](https://img.shields.io/badge/Slack-4A154B?logo=slack&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000?logo=notion&logoColor=white)
![Discord](https://img.shields.io/badge/Discord-5865F2?logo=discord&logoColor=white)

</br>

## 기술적 의사결정

- 📖 공통 라이브러리 설계 및 의존성 관리 전략 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/공통-라이브러리-설계-및-의존성-관리-전략)

- 📨 서비스 간 통신 방식 결정 (동기 vs 비동기) → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/서비스-간-통신-방식-결정)

- 📈 성능 최적화를 위한 배치 및 캐시 전략 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/성능-최적화를-위한-배치-및-캐시-전략)

- 👨‍🔬 통합 모니터링 및 트레이싱 환경 구축 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/통합-모니터링-및-트레이싱-환경-구축)

- ❤️‍🩹 서비스 장애 대응 및 복구 전략 수립 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/서비스-장애-대응-및-복구-전략-수립)

- 🐳 배포 전략 결정 및 클라우드 인프라 설계 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/배포-전략-결정-및-클라우드-인프라-설계)

<br/>

## 트러블슈팅

- ♻️ MSA 환경에서 API Gateway 연결 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/MSA-환경에서-API-Gateway-연결-트러블슈팅)

- ♾️ RDS 커넥션 제한과 Hikari CP 충돌 문제 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/RDS-커넥션-제한과-Hikari-CP-충돌-문제)

- 💸 실시간 최저가 상품 반영을 위한 엔티티 구조 설계 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/실시간-최저가-상품-반영을-위한-엔티티-구조-설계)

- 🎟️ 배포 서버 배치 실행 시 직렬화 문제 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/배포-서버-배치-실행-시-직렬화-문제)

- ⚡️ 서킷브레이커 사용시 예외 문제 → [📝 Docs](https://github.com/Genie-Uss/genieus/wiki/서킷브레이커-사용시-예외-문제)

- 👨🏻‍🔬 실제 운영 환경 부하 테스트 및 성능 개선 → [📝 Docs](https://nonstop-fruit-c9c.notion.site/1e721dc3d2cd80feae63c02917fc07a4?pvs=4)
