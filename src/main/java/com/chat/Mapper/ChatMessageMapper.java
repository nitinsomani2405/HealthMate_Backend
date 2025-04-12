package com.chat.Mapper;


import com.chat.Dtos.ChatMessageDTO;
import com.chat.Models.ChatMessage;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.UUID;

@Component
public class ChatMessageMapper {

    public ChatMessage toEntity(ChatMessageDTO dto) {
        return ChatMessage.builder()
                .senderId(dto.getSenderId())
                .receiverId(dto.getReceiverId())
                .message(dto.getMessage())
                .fileUrl(dto.getFileUrl())
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    public ChatMessageDTO toDTO(ChatMessage entity) {
        return ChatMessageDTO.builder()
                .messageId(entity.getMessageId())
                .senderId(entity.getSenderId())
                .receiverId(entity.getReceiverId())
                .message(entity.getMessage())
                .fileUrl(entity.getFileUrl())
                .timestamp(entity.getTimestamp().toInstant().toString())
                .build();
    }
}
