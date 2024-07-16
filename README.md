# Chat App
Chat App using Websocket

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

## 2024.06.18 Update
+ RabbitMQ(External Broker)를 통해 클라이언트가 메세지를 Subscribe할 수 있도록 변경함
+ 이에 따라 웹 소켓 컨테이너 Scale-out이 가능함

