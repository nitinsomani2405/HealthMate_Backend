package com.chat.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private UUID messageId;        // will be null when sending, filled when receiving
    private UUID senderId;
    private UUID receiverId;
    private String message;        // optional if fileUrl is used
    private String fileUrl;        // optional if message is used
    private String timestamp;      // ISO timestamp string, filled when receiving
}
