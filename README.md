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

## Development & Results
https://okkkk-aanng.tistory.com/29

