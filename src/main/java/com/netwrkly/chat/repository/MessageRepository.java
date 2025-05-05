package com.netwrkly.chat.repository;

import com.netwrkly.chat.model.Chat;
import com.netwrkly.chat.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByChatOrderByCreatedAtDesc(Chat chat, Pageable pageable);
} 