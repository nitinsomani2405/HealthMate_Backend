package com.chat.Repository;


import com.chat.Models.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    // Fetch all messages where sender and receiver are either way
    List<ChatMessage> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
            UUID senderId, UUID receiverId, UUID receiverId2, UUID senderId2
    );
}

