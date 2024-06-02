# Chat App
Chat App using Websocket

## Quick Start in Local

### 1. Create React App Image
+ git clone https://github.com/chrismrkr/chat-app-frontend.git
+ Build Image with Dockerfile : ```docker build -t chat-app-frontend ./```

### 2. Create SpringBoot App Image
+ git clone https://github.com/chrismrkr/chat-app.git
+ Build Image with Dockerfile : ```docker build -t chat-app-backend ./```

### 3. Run in Docker Compose
+ ```docker compose up -d```

### 4. Start
+ http://localhost:80/login
