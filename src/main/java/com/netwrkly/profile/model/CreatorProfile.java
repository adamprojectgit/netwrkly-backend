package com.netwrkly.profile.model;

import com.netwrkly.auth.model.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "creator_profiles")
public class CreatorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String bio;
    private String location;
    private String website;
    private String videoIntroUrl;

    @ElementCollection
    @CollectionTable(name = "creator_content_reel_urls", joinColumns = @JoinColumn(name = "creator_id"))
    @Column(name = "url")
    private List<String> contentReelUrls;

    @ElementCollection
    @CollectionTable(name = "creator_niches", joinColumns = @JoinColumn(name = "creator_id"))
    @Column(name = "niche")
    private List<String> niches;

    @ElementCollection
    @CollectionTable(name = "creator_content_types", joinColumns = @JoinColumn(name = "creator_id"))
    @Column(name = "content_type")
    private List<String> contentTypes;

    private Integer instagramFollowers;
    private Integer tiktokFollowers;
    private Integer youtubeSubscribers;
    private Integer averageEngagementRate;

    private boolean isVerified = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 