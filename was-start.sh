#!/bin/bash
# 부하테스트 실행 스크립트

set -e

echo "🧹 Docker 환경 완전 정리 중..."

# 기존 컨테이너 강제 중지 및 제거
docker compose -f docker-compose.was-ec2.yml down --remove-orphans --volumes 2>/dev/null || true
# 관련 컨테이너 강제 정리
docker container prune -f 2>/dev/null || true
# 네트워크 정리
docker network prune -f 2>/dev/null || true
# 볼륨 정리
docker volume ls -q | xargs -r docker volume rm 2>/dev/null || true

echo "✅ Docker 환경 정리 완료"

echo "🔧 Spring Boot 애플리케이션 빌드 중..."
cd backend
./gradlew clean build -x test --no-build-cache --no-daemon
cd ..

echo "🚀 Docker Compose 시작 중..."
echo "🔧 모든 이미지 강제 재빌드 중..."
docker compose -f docker-compose.was-ec2.yml build --no-cache
echo "🐳 모든 서비스 시작 중..."
docker compose -f docker-compose.was-ec2.yml up -d

echo "⏳ 서비스 준비 대기 중..."
sleep 10

# 백엔드 서비스 준비 대기
echo "🔍 백엔드 서비스 확인 중..."
for i in {1..60}; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "✅ 백엔드 서비스 준비 완료"
        break
    fi
    echo "백엔드 서비스 대기 중... ($i/60)"
    echo "📋 최근 백엔드 로그:"
    docker compose -f docker-compose.was-ec2.yml logs --tail=20 backend
    sleep 5
done

# DataInitializer 완료 확인
echo "🔍 DataInitializer 완료 확인 중..."
for i in {1..30}; do
    if curl -s -X POST http://localhost:8080/api/auth/login \
        -H "Content-Type: application/json" \
        -d '{"nickname":"user5000"}' > /dev/null 2>&1; then
        echo "✅ DataInitializer 완료 확인"
        break
    fi
    echo "DataInitializer 대기 중... ($i/30)"
    sleep 2
done