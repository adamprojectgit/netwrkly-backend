package com.netwrkly.profile.controller;

import com.netwrkly.profile.model.BrandProfile;
import com.netwrkly.profile.service.BrandProfileService;
import com.netwrkly.auth.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/brand-profiles")
@RequiredArgsConstructor
@Slf4j
public class BrandProfileController {
    private final BrandProfileService brandProfileService;

    @GetMapping
    public ResponseEntity<BrandProfile> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(brandProfileService.getProfile(user));
    }

    @PostMapping
    public ResponseEntity<BrandProfile> createOrUpdateProfile(
            @RequestBody BrandProfile profile,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(brandProfileService.createOrUpdateProfile(profile, user));
    }

    @PutMapping(path = "/me/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_BRAND')")
    public ResponseEntity<String> uploadLogo(@RequestParam("file") MultipartFile file) {
        log.info("Uploading logo for brand profile");
        String logoUrl = brandProfileService.storeLogo(file);
        return ResponseEntity.ok(logoUrl);
    }

    @DeleteMapping("/logo/{filename}")
    public ResponseEntity<Void> deleteLogo(@PathVariable String filename) {
        brandProfileService.deleteLogo(filename);
        return ResponseEntity.ok().build();
    }
} 