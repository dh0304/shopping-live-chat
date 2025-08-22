package com.shoppinglive.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ChatRoom {
    
    @Id
    @Column(name = "chat_room_id")
    private String chatRoomId;
    
    @Column(name = "room_name")
    private String roomName;
    
    @Column(name = "user_count", nullable = false)
    @Builder.Default
    private Integer userCount = 0;

    // 동시성 테스트, 낙관적 락을 위한 필드, 사용안하면 주석처리할 것
//    @Version
//    @Column(name = "version")
//    private Long version;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void incrementUserCount() {
        this.userCount++;
    }

    public void decrementUserCount() {
        if(this.userCount > 0) {
            this.userCount--;
        }
    }
}