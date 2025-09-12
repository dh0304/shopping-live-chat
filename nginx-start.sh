#!/bin/bash

# nginx 컨테이너 중지 및 제거 (있다면)
docker stop nginx-proxy 2>/dev/null || true
docker rm nginx-proxy 2>/dev/null || true

# nginx 이미지 빌드
docker build -f Dockerfile.nginx -t nginx-proxy .

# nginx 컨테이너 실행
docker run -d --name nginx-proxy -p 80:80 nginx-proxy

echo "nginx proxy started on port 80"