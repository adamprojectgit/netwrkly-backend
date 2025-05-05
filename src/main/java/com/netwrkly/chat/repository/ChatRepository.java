package com.netwrkly.chat.repository;

import com.netwrkly.auth.model.User;
import com.netwrkly.chat.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByBrandOrCreatorOrderByLastMessageAtDesc(User brand, User creator);
} 