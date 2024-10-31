# 📈 Trading Trends
![Trading Trends 2000](https://github.com/user-attachments/assets/42402f76-533b-4dc1-a75e-1b37aee378f0)

![image (14)](https://github.com/user-attachments/assets/9cf6c86e-7d54-41dc-9752-3f3d6bbb4943)

|분류|내용|
|---|---|
|주제|주식/코인 인사이트 플랫폼|
|팀원 구성|[👑조인희](https://github.com/InHeeS) [김민철](https://github.com/kmc198989) [이예지](https://github.com/yezyaa) [임예이](https://github.com/coding-911)|
|개발 기간|2024.09.24 ~ 2024.10.24|
|ERD|[🔗Link](https://www.erdcloud.com/d/RvPEp4mEbrSbEakdc)|
|API 명세서|[🔗Link](https://horse-giver-fbd.notion.site/API-fff2ebde9ffc8110b0d5f1328b0a50d5)|
|API 시나리오 명세서|[🔗Link](https://horse-giver-fbd.notion.site/API-11f2ebde9ffc8044987af100431c263c)|
|배포 링크|http://ec2-43-203-146-138.ap-northeast-2.compute.amazonaws.com:19090|

<br/><br/>

## **🎯 프로젝트 개요**
### **프로젝트 소개**
국내시장 **주식 및 코인** 거래자들을 위한 **인사이트**를 주는 시스템입니다.<br/><br/>

### **프로젝트 목표**
대규모 트래픽에도 내결함성과 고가용성을 갖춘 구조를 통해 실시간 데이터 및 대용량 데이터를 안정적으로 클라이언트에게 전달하는 시스템을 구현합니다.
<br/><br/>

### **기획 의도**
- 기업 재무 정보 제공
- 공시 자료 원본(비정형 데이터) 제공
- 실시간 종목 정보(주식, 코인) 제공
- 사용자 편의를 위한 API 제공
- 내결함성과 실시간 모니터링 기능을 갖춘 인프라 구축<br/><br/><br/>

## **☁️ 인프라 설계도**
![cicd](https://github.com/user-attachments/assets/3c1f5302-a73f-4100-bf1b-b471ab6f7036)
<br/><br/><br/>

## **🛠️ 개발 환경**
### 기술 스택
- Backend ![Java](https://img.shields.io/badge/Java17-%23ED8B00.svg?style=square&logo=openjdk&logoColor=white) <img src="https://img.shields.io/badge/Spring%20Boot%203.3.4-6DB33F?style=square&logo=springboot&logoColor=white"> ![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=square&logo=Spring&logoColor=white) ![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-6DB33F?style=square&logo=spring&logoColor=white) ![Spring Batch](https://img.shields.io/badge/Spring%20Batch-6DB33F?style=square&logo=spring&logoColor=white) ![JWT](https://img.shields.io/badge/JWT-black?style=square&logo=JSON%20web%20tokens) ![Gradle](https://img.shields.io/badge/Gradle%207.6-02303A.svg?style=square&logo=Gradle&logoColor=white) ![Docker](https://img.shields.io/badge/Docker-%230db7ed.svg?style=square&logo=docker&logoColor=white) 
  ![DockerHub](https://img.shields.io/badge/DockerHub-%230db7ed.svg?style=square&logo=docker&logoColor=white) 
- Database ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1.svg?style=square&logo=PostgreSQL&logoColor=white) ![Amazon ElastiCache](https://img.shields.io/badge/Amazon%20ElastiCache-527FFF?style=square&logo=Amazon%20AWS&logoColor=white)
 ![AmazonDynamoDB](https://img.shields.io/badge/Amazon%20RDS-527FFF?style=square&logo=AmazonRDS&logoColor=white) <img src="https://img.shields.io/badge/Redis-DC382D?style=square&logo=Redis&logoColor=white">
- Search/Indexing ![Elastic Cloud](https://img.shields.io/badge/Elastic%20Cloud-005571?style=square&logo=elasticsearch&logoColor=white) 
  ![Elasticsearch](https://img.shields.io/badge/Elasticsearch%208.15.2-005571?style=square&logo=elasticsearch&logoColor=white) 
  ![Kibana](https://img.shields.io/badge/Kibana%208.15.2-005571?style=square&logo=kibana&logoColor=white)
- CI/CD ![Ubuntu](https://img.shields.io/badge/Ubuntu-E95420?style=square&logo=ubuntu&logoColor=white) <img src="https://img.shields.io/badge/Amazon%20EC2-FF9900?style=square&logo=Amazon%20EC2&logoColor=white">
  ![GitActions](https://img.shields.io/badge/GitActions-2088FF?style=square&logo=GitHub%20Actions&logoColor=white) ![SSH](https://img.shields.io/badge/SSH-008000?style=square&logo=OpenSSH&logoColor=white)
- Streaming ![Kafka](https://img.shields.io/badge/Kafka-231F20.svg?style=square&logo=apachekafka&logoColor=white) 
  ![WebSocket](https://img.shields.io/badge/WebSocket-010101.svg?style=square&logo=websocket&logoColor=white)
- Monitoring ![Elasticsearch](https://img.shields.io/badge/Elasticsearch%208.15.2-005571?style=square&logo=elasticsearch&logoColor=white) 
  ![Grafana](https://img.shields.io/badge/Grafana-F46800?style=square&logo=grafana&logoColor=white) 
  ![JMX Agents](https://img.shields.io/badge/JMX%20Agents-000000?style=square) 
  ![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=square&logo=prometheus&logoColor=white) 
  ![Filebeat](https://img.shields.io/badge/Filebeat-0073A8?style=square&logo=elastic&logoColor=white) 
  ![Kibana](https://img.shields.io/badge/Kibana%208.15.2-005571?style=square&logo=kibana&logoColor=white) 
  ![Metricbeat](https://img.shields.io/badge/Metricbeat-0073A8?style=square&logo=elastic&logoColor=white)
- Tools ![GitHub](https://img.shields.io/badge/Github-%23121011.svg?style=square&logo=github&logoColor=white) ![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-000000.svg?style=square&logo=intellij-idea&logoColor=white) ![Postman](https://img.shields.io/badge/Postman-FF6C37?style=square&logo=postman&logoColor=white) ![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=square&logo=notion&logoColor=white)<br/><br/>

### **커밋 컨벤션**
|타입|내용|
|---|---|
|feat|새로운 기능 추가|
|fix|기능 수정, 버그 해결|
|refactor|코드 리팩토링|
|test|테스트 코드|
|remove|폴더/파일 삭제|
|docs|문서 변경|
|chore|패키지 매니저 수정, 그 외 기타 수정|
|comment|주석 추가/변경|

<br/><br/>

## **💡 주요 기능**
<details>
<summary><strong>JWT 기반 로그인 기능 구현</strong></summary>

- AccessToken과 RefreshToken을 활용한 인증 관리
- 로그아웃 시 블랙리스트 기능을 통한 토큰 무효화 처리

</details>

<details>
<summary><strong>기업 정보 제공 기능</strong></summary>
<img src="https://github.com/user-attachments/assets/f1559456-dbc6-4d1e-a98d-1fd94b27dbf0" alt="기업 재무 및 공시 자료 수집" />

  
- 매일 오전 4시 기업 고유 번호, 기업 주요 재무 지표 수집/적재
- 매일 오전 5시 공시 검색 데이터, 공시 자료 원본 수집/적재
- 관심 기업 등록/삭제/조회
- 기업 주요 재무 지표 검색/전체 조회/상세 조회
- 조회수에 따른 핫토픽 top N 기업 조회

</details>

<details>
<summary><strong>주식/코인 정보 제공 기능</strong></summary>
<img src="https://github.com/user-attachments/assets/5959124c-971c-478d-b099-5710c7a4589f" alt="실시간 데이터 전달 방식" />

- 실시간 주식/코인 데이터 수집
- 종목 상세 정보 전체 조회/상세 조회
- 관심 종목 등록/삭제/조회
- 종목 채널 발행에 따른 유저별 수신 데이터 처리
- 사용자ID 기반 구독 종목의 실시간 전송
- 등락률에 따른 BEST/WORST 코인 분별 캐싱 처리

</details>

<details>
<summary><strong>로그/성능 모니터링</strong></summary>
<img src="https://github.com/user-attachments/assets/63ac0969-3a78-42ff-9e79-b5dd6284d768" alt="서비스 로깅 시스템" />
- 서비스 로그 모니터링
- 서비스 성능 모니터링

</details>

<details>
<summary><strong>인프라 장애 대응</strong></summary>
<img src="https://github.com/user-attachments/assets/f673ed87-e39c-4bb8-a586-6dd09423eb0b" alt="레디스 장애 대응 구축" />
- 실시간 데이터 수집 및 제공에 따른 장애 대응

</details>
<br/><br/>

## 🪄 적용 기술

<details>
<summary><strong>기업</strong></summary>

- [Spring Batch를 통한 스케줄링](https://horse-giver-fbd.notion.site/Spring-Batch-53c038ac64964e5c8375d5a8b8f3f3ee)
- [Elasticsearch를 통한 비정형 데이터 적재 (인덱싱)](https://horse-giver-fbd.notion.site/Elasticsearch-78168933061f4893af40efb5b513e458)
- [Redis를 활용한 핫토픽 Top N 기업 조회](https://horse-giver-fbd.notion.site/Redis-Top-N-9ae433dda54f42629ec4ee7afaaba8b8)
- [Elasticsearch 기반 검색 기능](https://horse-giver-fbd.notion.site/Elasticsearch-f8cee813782e45bba72fe62173505919)

</details>

<details>
<summary><strong>주식/코인</strong></summary>

- [WebSocket을 통한 실시간 데이터 수집](https://horse-giver-fbd.notion.site/WebSocket-fc8c69f716454502af6ce20a5d5e8e17)
- [Redis Pub/Sub 기능을 활용한 채널 발행 및 유저의 구독 정보에 따른 데이터 처리](https://horse-giver-fbd.notion.site/Redis-Pub-Sub-85285e07f8d74f8ba5be89a026bdec1b)
- [사용자ID 기반 WebSocket을 통한 구독된 종목의 실시간 전달](https://horse-giver-fbd.notion.site/ID-WebSocket-09e6dbea56c54cf9b725a39fe9019257)

</details>

<details>
<summary><strong>모니터링</strong></summary>

- [Elastic Stack을 활용한 서비스 로그 모니터링](https://horse-giver-fbd.notion.site/Elastic-Stack-e861d910892d4b72b3876528e3755a95)
- [JMX Exporter, Kafka Exporter, 그리고 Kafdrop을 활용한 Kafka 클러스터 성능 모니터링](https://horse-giver-fbd.notion.site/JMX-Exporter-Kafka-Exporter-Kafdrop-Kafka-1740a85eb5f94150977112b72273e81c)
- [Metricbeat를 통한 ELK 성능 모니터링](https://horse-giver-fbd.notion.site/Metricbeat-ELK-1262ebde9ffc80b8bf58daf1c4bf5646)
- Actuator, Prometheus, Grafana를 이용한 애플리케이션 성능 모니터링

</details>

<details>
<summary><strong>장애 대응</strong></summary>

- [Redis Sentinel 을 활용한 장애 대응 구축](https://horse-giver-fbd.notion.site/Redis-Sentinel-1262ebde9ffc80e689efff64aa261157)
- [브로커 다중화를 통한 kafka 클러스터 구성](https://horse-giver-fbd.notion.site/kafka-8454aff0d3344d0099c8e77777b6c5ea)

</details>

<details>
<summary><strong>인증/인가</strong></summary>

- [JWT 기반 인증 및 인가 시스템 구현](https://horse-giver-fbd.notion.site/JWT-26b73fc2e1fc40f283b84b15af014c35)

</details>

---

<details>
<summary><strong>API 부하 테스트</strong></summary>

- [검색 성능 결과](https://horse-giver-fbd.notion.site/1262ebde9ffc807484c6f9b7676f25fe)
- [DB 조회 성능 결과](https://horse-giver-fbd.notion.site/DB-1262ebde9ffc805e9bdbfcb46a5a7061)

</details>

<br/><br/>

## ❓ 기술적 의사결정
- [기술적 의사결정](https://horse-giver-fbd.notion.site/1262ebde9ffc8081a042c7185420bff8)
<br/><br/><br/>

## 💥 트러블 슈팅
- [Filebeat가 install 되지 않을 경우](https://horse-giver-fbd.notion.site/Filebeat-install-9e1828aa0c57475ea30d301d745f3ab9)
- [Redis Sentinel Node 통신 에러](https://horse-giver-fbd.notion.site/Redis-Sentinel-Node-1262ebde9ffc809db7f1ce820575b2dd)
- [클라이언트의 정보를 Websocket Session 에서 가져오지 못하는 문제](https://horse-giver-fbd.notion.site/Websocket-Session-1262ebde9ffc80e6a930db2eed0087d2)
<br/><br/><br/>
