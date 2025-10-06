-- 기존 데이터 및 테이블 삭제
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS user, chat_room, message, user_chat_room;
SET FOREIGN_KEY_CHECKS = 1;

-- 테이블 생성 (Spring Boot에서 자동 생성되지만 명시적으로 정의)
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date DATETIME(6),
    deleted_date DATETIME(6),
    last_modified_date DATETIME(6),
    nickname VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS chat_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date DATETIME(6),
    deleted_date DATETIME(6),
    last_modified_date DATETIME(6),
    room_name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date DATETIME(6),
    deleted_date DATETIME(6),
    last_modified_date DATETIME(6),
    content TEXT,
    user_id BIGINT,
    chat_room_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (chat_room_id) REFERENCES chat_room(id)
);

CREATE TABLE IF NOT EXISTS user_chat_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date DATETIME(6),
    deleted_date DATETIME(6),
    last_modified_date DATETIME(6),
    user_id BIGINT,
    chat_room_id BIGINT,
    exit_time DATETIME(6),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (chat_room_id) REFERENCES chat_room(id)
);

-- 10,000명의 사용자 생성
INSERT INTO user (created_date, last_modified_date, nickname) 
SELECT 
    NOW(),
    NOW(),
    CONCAT('user', n.num)
FROM (
    SELECT 
        @row := @row + 1 as num
    FROM 
        (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t1,
        (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t2,
        (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t3,
        (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t4,
        (SELECT @row := 0) r
    WHERE @row < 10000
) n;

-- 채팅방 생성
INSERT INTO chat_room (created_date, last_modified_date, room_name, description) VALUES
(NOW(), NOW(), '올가을 스타일 완성! 패션 핫템 특집', '계절마다 달라지는 트렌드, 이번 시즌 놓치면 안 될 필수 아이템을 준비했습니다. 라이브 중에만 가능한 한정 할인과 스타일링 팁도 함께 만나보세요.'),
(NOW(), NOW(), '오늘의 밥상, 신선 특가 식품전', '산지 직송 신선 식품부터 인기 간편식까지! 맛과 가격 모두 잡은 특별한 구성으로 준비했습니다. 방송 중 깜짝 증정 이벤트도 함께 즐겨보세요.'),
(NOW(), NOW(), '생활이 편해지는 스마트 가전 모음', '집안일을 더 쉽고 똑똑하게! 최신 가전제품을 라이브 단독 혜택으로 만나보세요. 실시간으로 제품 사용법과 꿀팁도 알려드립니다.');