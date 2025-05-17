package com.netwrkly.brief.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.netwrkly.auth.model.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "briefs")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Brief {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String background;

    @Column(columnDefinition = "TEXT", name = "ask_description")
    private String ask;

    @Column(columnDefinition = "TEXT")
    private String deliverables;

    private String budget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BriefStatus status = BriefStatus.DRAFT;

    @Column(nullable = false)
    private int responses = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creator_id", nullable = false)
    @JsonIgnoreProperties({"password", "verificationToken", "resetPasswordToken", "resetPasswordTokenExpiry", "enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "authorities"})
    private User creator;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", nullable = false)
    @JsonIgnoreProperties({"password", "verificationToken", "resetPasswordToken", "resetPasswordTokenExpiry", "enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "authorities"})
    private User brand;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 