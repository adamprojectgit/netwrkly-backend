package com.netwrkly.profile.model;

import com.netwrkly.auth.model.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "brand_profiles")
public class BrandProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = true)
    private String companyName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = true)
    private String industry;

    private String website;

    @ElementCollection
    @CollectionTable(name = "brand_preferred_niches", joinColumns = @JoinColumn(name = "brand_id"))
    @Column(name = "niche")
    private List<String> preferredNiches = new ArrayList<>();

    private String logoUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 