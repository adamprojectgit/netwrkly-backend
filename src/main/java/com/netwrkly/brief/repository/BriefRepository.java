package com.netwrkly.brief.repository;

import com.netwrkly.brief.model.Brief;
import com.netwrkly.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BriefRepository extends JpaRepository<Brief, Long> {
    List<Brief> findByCreatorOrderByCreatedAtDesc(User creator);
} 