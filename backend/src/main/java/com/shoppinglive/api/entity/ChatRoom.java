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
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}