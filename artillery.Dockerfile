FROM artilleryio/artillery:latest

# 작업 디렉토리 설정
WORKDIR /app

# package.json 생성 및 ws 모듈 설치 (로컬 설치)
RUN echo '{"name": "artillery-custom", "version": "1.0.0", "dependencies": {"ws": "^8.0.0"}}' > package.json && \
    npm install

# 설정 파일들을 복사
COPY artillery-load-test.yml /app/
COPY artillery-functions.js /app/

# Artillery 실행 명령어
CMD ["run", "/app/artillery-load-test.yml"]