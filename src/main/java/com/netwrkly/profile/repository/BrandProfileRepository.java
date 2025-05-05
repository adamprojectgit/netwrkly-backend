package com.netwrkly.profile.repository;

import com.netwrkly.profile.model.BrandProfile;
import com.netwrkly.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BrandProfileRepository extends JpaRepository<BrandProfile, Long> {
    Optional<BrandProfile> findByUser(User user);
    List<BrandProfile> findByIndustry(String industry);
    List<BrandProfile> findByPreferredNichesContaining(String niche);
} 