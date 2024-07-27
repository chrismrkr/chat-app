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

![externalbroker-idempotency drawio](https://github.com/user-attachments/assets/5c8e5e4a-9d01-4bd6-8e8f-59d45983d8b5)

클라이언트가 웹 소켓을 통해 서버와 연결되면, Connection 마다 고유 Session ID를 갖는다.

그리고, Session 내의 Outbound Channel을 통해 메세지를 송신하고, Inbound Channel을 통해 메세지를 수신한다.

### 문제점

참고자료: https://www.rabbitmq.com/docs/confirms

아래는 RabbitMQ에서의 일부분을 발췌하였음

```
When manual acknowledgements are used, any delivery (message) that was not acked is automatically requeued when the channel (or connection) on which the delivery happened is closed.
...(중략)
Due to this behavior, consumers must be prepared to handle redeliveries and otherwise be implemented with idempotence in mind. ...(중략)
```

요컨데, RabbitMQ Broker는 Consumer로 부터 Ack를 받아야 Queue에서 메세지를 제거하고, Ack를 받지 못한다면 메세지를 Requeue 한다는 것을 의미한다.

그러므로, Consumer가 메세지를 받았지만 네트워크 문제 등으로 Ack가 Broker에 도착하지 않으면, Consumer는 동일한 메세지를 중복 수신하는 문제가 발생한다.

### 해결방법

#### 1. 
