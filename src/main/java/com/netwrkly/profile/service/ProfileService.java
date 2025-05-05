package com.netwrkly.profile.service;

import com.netwrkly.profile.model.BrandProfile;
import com.netwrkly.profile.repository.BrandProfileRepository;
import com.netwrkly.auth.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProfileService {
    private final BrandProfileRepository brandProfileRepository;

    @Autowired
    public ProfileService(BrandProfileRepository brandProfileRepository) {
        this.brandProfileRepository = brandProfileRepository;
    }

    public BrandProfile getProfile(User user) {
        return brandProfileRepository.findByUser(user)
                .orElse(new BrandProfile());
    }

    public BrandProfile updateProfile(BrandProfile profile, User user) {
        Optional<BrandProfile> existingProfile = brandProfileRepository.findByUser(user);
        
        if (existingProfile.isPresent()) {
            BrandProfile updatedProfile = existingProfile.get();
            updatedProfile.setCompanyName(profile.getCompanyName());
            updatedProfile.setDescription(profile.getDescription());
            updatedProfile.setIndustry(profile.getIndustry());
            updatedProfile.setWebsite(profile.getWebsite());
            updatedProfile.setPreferredNiches(profile.getPreferredNiches());
            updatedProfile.setLogoUrl(profile.getLogoUrl());
            return brandProfileRepository.save(updatedProfile);
        } else {
            profile.setUser(user);
            return brandProfileRepository.save(profile);
        }
    }

    public List<BrandProfile> searchBrandsByIndustry(String industry) {
        if (industry != null) {
            return brandProfileRepository.findByIndustry(industry);
        }
        return brandProfileRepository.findAll();
    }
} 