# Chat App
Chat App using Websocket

![chatapp-infra drawio](https://github.com/user-attachments/assets/6af0de7b-222a-4f77-a3c4-8cae499e8c46)

## Quick Start in Local

### 1. Create React App Image
+ git clone https://github.com/chrismrkr/chat-app-frontend.git
+ Build Image with Dockerfile : ```docker build -t chat-app-frontend ./```

### 2. Create SpringBoot App Image
+ git clone https://github.com/chrismrkr/chat-app.git
+ Build Jar File : ./gradlew clean build -x test 
+ Build Image with Dockerfile : ```docker build -t chat-app-backend ./```

### 3. Run in Docker Compose
+ ```docker compose up -d```

### 4. Start
+ http://localhost:80/login

## 2024.06.18 Update : External Broker
+ RabbitMQ(External Broker)를 통해 클라이언트가 메세지를 Subscribe할 수 있도록 변경함
+ 이에 따라 웹 소켓 컨테이너 Scale-out이 가능함

## 2024.07.19 Update : Health Check
+ 헬스 체크
  + Server - Client 헬스 체크 : StompClient 내장 heartbeat 및 reconnectDelay 기능 활용
  + Server - External Broker 헬스 체크 : TaskScheduler를 활용한 External Broker 상태 점검 후, 결과 Client에 전달

## 2024.07.27 Update : Idempotence

![externalbroker-idempotency drawio (1)](https://github.com/user-attachments/assets/53c8124a-58c8-4c52-9286-2e17ff99b40d)

클라이언트가 웹 소켓을 통해 서버와 연결되면 Session이 생성되고, Session 마다 Unique ID를 갖는다.

그리고, Session 내의 Inbound Channel을 통해 메세지를 송신하고, Outbound Channel을 통해 메세지를 수신한다.

### 문제점

참고자료: https://www.rabbitmq.com/docs/confirms

아래는 RabbitMQ Document 일부분을 발췌하였음

```
When manual acknowledgements are used, any delivery (message) that was not acked is automatically requeued when the channel (or connection) on which the delivery happened is closed.
...(중략)
Due to this behavior, consumers must be prepared to handle redeliveries and otherwise be implemented with idempotence in mind. ...(중략)
```

요컨데, RabbitMQ Broker는 Consumer로 부터 Ack를 받아야 Queue에서 메세지를 제거하고, Ack를 받지 못한다면 메세지를 Requeue 한다는 것을 의미한다.

그러므로, Consumer가 메세지를 받았지만 네트워크 문제 등으로 Broker에 Ack가 도착하지 않으면, Consumer는 동일한 메세지를 중복 수신하는 문제가 발생한다. (Idempotence 위반)

### 해결방법

#### 1. 메세지 수신 History 저장

TCP 프로토콜 메세지 중복 제거 관련 알고리즘을 응용하여 개발하였음

- 클라이언트가 연결되어 Session이 생성되면, 서버는 OutboundChannelConnected Event를 받음
- 해당 이벤트가 발생하면, 서버에 (ReceiverSessionId -> SenderSessionId -> MessageSequence)를 저장할 수 있도록 Map 생성
- Map에는 "현재 Session"이 "다른 Session"으로 부터 메세지를 "몇번째 Sequence"까지 받았는지를 저장함
```java
@EventListener
public void handleOutboundChannelConnectedEvent(SessionConnectedEvent event) {
  StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
  log.info("[MESSAGE HISTORY CREATED] " + accessor.getSessionId());
  outboundChannelHistoryRepository.createSessionHistory(accessor.getSessionId());
}
```
- Outbound Channel에 Interceptor를 추가하여 클라이언트에 메세지를 전송하기 전, 이미 전송된 MessageSequence인지를 확인함
```java
public Message<?> preSend(Message<?> message, MessageChannel channel) {
  // TODO. Duplicate Message Sequence Check
}
```
- 만약 이전에 전송된 메세지가 아니라면, 전송이 완료된 시점(Ack)에 Map을 Update함
```java
public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception ex) {
  // TODO. Message Sequence which can be received Update
}
```
- 클라이언트 연결이 종료될 때, History Map을 삭제함
```java
@EventListener
public void handleOutboundChannelDisconnectEvent(SessionDisconnectEvent event) {
  StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
  log.info("[Message HISTORY DISCARDED] " + accessor.getSessionId());
  outboundChannelHistoryRepository.deleteSessionHistory(accessor.getSessionId());
}
```
**한계: 메세지 전송이 완료되어 서버가 Ack를 받기 이전에 동일한 메세지가 다시 전송되면, 메세지 중복 수신을 막을 수 없음**
```
SESSION1: ----->CheckSeq(SEQ: 0 VALID)------------->SEND(SEQ: 0)-------------->UpdateSeq(SEQ: 1 VALID)-------------
SESSION2: ------------------>CheckSeq(SEQ: 0 VALID)------------->SEND(SEQ: 0)-------------->UpdateSeq(SEQ: 1 VALID)
```

#### 2. Distributed Lock

위 한계를 극복하기 위해 Redis Distributed Lock 기능을 도입함

- CheckSeq 이전, ```SETNX ({ReceiverSessionId}{SenderSessionId}{MessageSeq}) (VAL)```
- UpdateSeq 이후, ```DEL ({ReceiverSessionId}{SenderSessionId}{MessageSeq})```


## Performance Tests

클라이언트가 메세지를 수신받는 것을 기준으로 측정함

### 1차 성능 테스트
- 하드웨어 성능
  - CPU: 2 Core
  - RAM: 4GB
  - DISK: 20GB
#### 1. 50 RPS(Request per Second)
- 평균 성공 수신 속도: 53ms
- 오류율: 0%
#### 2. 100 RPS
- 오류율: 10%(3000ms Timeout)
- 현상
```
Response code:Websocket I/O error
Response message:WebSocket I/O error: Read timed out
```
- 트러블 슈팅
  - CPU 및 메모리 사용률 모두 정상 수준이었고, ```iftop``` 명령어를 통해 네트워크 사용량을 모니터링 하였음
  - Case1. 10초 내 서버 -> 클라이언트 송신 네트워크 평균 사용량이 약 30KB인 상태에서, 100 RPS 테스트
    - 오류율 0%, 평균 수신 속도: 55ms **(정상)**
  - Case2. 10초 내 서버 -> 클라이언트 송신 네트워크 평균 사용량이 약 800KB인 상태에서, 100 RPS 테스트
    - 오류율 16%, 네트워크 대역폭 확인 필요
