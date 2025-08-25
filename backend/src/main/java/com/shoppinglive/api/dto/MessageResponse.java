package com.shoppinglive.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    
    private Long id;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
}