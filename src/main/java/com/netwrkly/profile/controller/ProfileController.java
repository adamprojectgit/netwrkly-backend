package com.netwrkly.profile.controller;

import com.netwrkly.profile.model.BrandProfile;
import com.netwrkly.profile.service.ProfileService;
import com.netwrkly.auth.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/brand")
    public ResponseEntity<BrandProfile> getBrandProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(profileService.getProfile(user));
    }

    @PostMapping("/brand")
    public ResponseEntity<BrandProfile> updateBrandProfile(
            @RequestBody BrandProfile profile,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(profileService.updateProfile(profile, user));
    }

    @GetMapping("/brand/search")
    public ResponseEntity<List<BrandProfile>> searchBrandsByIndustry(
            @RequestParam(required = false) String industry
    ) {
        return ResponseEntity.ok(profileService.searchBrandsByIndustry(industry));
    }
} 