package com.netwrkly.profile.repository;

import com.netwrkly.profile.model.CreatorProfile;
import com.netwrkly.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreatorProfileRepository extends JpaRepository<CreatorProfile, Long> {
    Optional<CreatorProfile> findByUser(User user);
    List<CreatorProfile> findByNichesContaining(String niche);
    List<CreatorProfile> findByContentTypesContaining(String contentType);
    List<CreatorProfile> findByIsVerifiedTrue();
    List<CreatorProfile> findByNichesContainingAndContentTypesContaining(String niche, String contentType);
} 