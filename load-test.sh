#!/bin/bash

# 부하테스트 실행 스크립트
# Docker 네트워크 오류 방지를 위한 완전 정리 후 재시작

set -e

echo "🧹 Docker 환경 완전 정리 중..."

# 기존 컨테이너 강제 중지 및 제거
docker-compose down --remove-orphans --volumes 2>/dev/null || true

# 관련 컨테이너 강제 정리
docker container prune -f 2>/dev/null || true

# 네트워크 정리
docker network prune -f 2>/dev/null || true

# 볼륨 정리 (선택사항 - 데이터 초기화됨)
#docker volume prune -f 2>/dev/null || true

# Grafana 볼륨 제외하고 정리
docker volume ls -q | grep -v "grafana_data" | xargs -r docker volume rm 2>/dev/null || true

echo "✅ Docker 환경 정리 완료"

echo "🔧 Spring Boot 애플리케이션 빌드 중..."
cd backend
./gradlew clean build -x test --no-build-cache --no-daemon
cd ..

echo "🚀 Docker Compose 시작 중..."
echo "🔧 모든 이미지 강제 재빌드 중..."
docker-compose build --no-cache
echo "🐳 모든 서비스 시작 중..."
docker-compose up -d

echo "⏳ 서비스 준비 대기 중..."
sleep 10

# MySQL 연결 대기
echo "🔍 MySQL 연결 확인 중..."
for i in {1..30}; do
    if docker-compose exec -T mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
        echo "✅ MySQL 연결 완료"
        break
    fi
    echo "대기 중... ($i/30)"
    sleep 2
done

# 백엔드 서비스 준비 대기
echo "🔍 백엔드 서비스 확인 중..."
for i in {1..60}; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "✅ 백엔드 서비스 준비 완료"
        break
    fi
    echo "백엔드 서비스 대기 중... ($i/60)"
    sleep 3
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

# Artillery 컨테이너 실행 (detached 모드로 실행하여 로그를 실시간으로 볼 수 있음)
#docker-compose --profile test up --remove-orphans

docker-compose --profile up