
# 1,2,4
postgres, elk, redis(단일, auth)



# scenario-3-1.http 테스트시 필요한 준비사항 

1. docker-compose -f docker-compose-kafka.yml up -d 
   - ![img.png](images/img.png)

2. scenario-3-1.http Step 순서대로 실행하면 됩니다.

3. STEP 1에 해당하는 유저 ID 별 웹소켓 연결 API 를 테스트하기 위해서는 Postman 에서 Websocket Template 필요합니다. 
   - https://syk531.tistory.com/95
   - 해당 블로그를 참고해서 WebSocket 테스트 환경을 구성합니다.(간단합니다)
   - ![img.png](images/img-websocket.png)
   - url : ws://localhost:19100/ws/connect/coin-data-per-user?userId=1
   
4. 실행해야 되는 module application
   - Market, Coin


# scenario-3-2.http 테스트시 필요한 준비사항

1. docker-compose -f docker-compose-kafka.yml up -d
    - ![img.png](images/img.png)

2. scenario-3-2.http Step 순서대로 실행하면 됩니다.

3. STEP 1에 해당하는 유저 ID 별 웹소켓 연결 API 를 테스트하기 위해서는 Postman 에서 Websocket Template 필요합니다.
    - https://syk531.tistory.com/95
    - 해당 블로그를 참고해서 WebSocket 테스트 환경을 구성합니다.(간단합니다)
    - ![img.png](images/img-websocket.png)
    - url 예시 : ws://localhost:19100/ws/connect/coin-data-per-user?userId=1

4. 루트디렉토리에 .env.dev 파일을 작성해주세요
   - ![img.png](images/img-env.png)

5. 실행해야 되는 module application
   - Market, Stock

# 주의사항
1. 두개의 docker-compose 실행시 컴퓨터가 뻗을 수 있기 때문에 각 시나리오에 필요한 docker-compose 파일만 실행해주세요
2. scenario-3-2는 한국투자증권 api 를 발급 받아야 진행 가능합니다(app-key, secret-key). 한국투자증권 계좌가 있으신분만 진행해주세요
   - url : https://securities.koreainvestment.com/main/customer/systemdown/RestAPIService.jsp
   - image : ![img.png](images/img-한투.png)