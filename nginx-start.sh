#!/bin/bash

# React 앱 빌드
echo "Building React app..."
cd frontend
npm install
npm run build
cd ..

# nginx 컨테이너 중지 및 제거 (있다면)
docker stop nginx 2>/dev/null || true
docker rm nginx 2>/dev/null || true

# nginx 이미지 빌드
docker build -f Dockerfile.nginx -t nginx .

# nginx 컨테이너 실행
docker run -d --name nginx --restart unless-stopped -p 80:80 nginx

echo "nginx started on port 80"