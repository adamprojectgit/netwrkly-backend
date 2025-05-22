package com.netwrkly.profile.controller;

import com.netwrkly.profile.model.BrandProfile;
import com.netwrkly.profile.service.BrandProfileService;
import com.netwrkly.auth.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/brand-profile")
public class BrandProfileController {
    private final BrandProfileService brandProfileService;

    @Autowired
    public BrandProfileController(BrandProfileService brandProfileService) {
        this.brandProfileService = brandProfileService;
    }

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

    @PostMapping("/logo")
    public ResponseEntity<String> uploadLogo(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null) {
                return ResponseEntity.badRequest().body("No file provided");
            }
            
            if (!file.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().body("Only image files are allowed");
            }
            
            String fileUrl = brandProfileService.uploadLogo(file);
            return ResponseEntity.ok(fileUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/logo/{filename}")
    public ResponseEntity<Void> deleteLogo(@PathVariable String filename) {
        brandProfileService.deleteLogo(filename);
        return ResponseEntity.ok().build();
    }
} 