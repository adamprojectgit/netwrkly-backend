package com.netwrkly.brief.service;

import com.netwrkly.brief.model.Brief;
import com.netwrkly.brief.model.BriefStatus;
import com.netwrkly.brief.repository.BriefRepository;
import com.netwrkly.brief.dto.CreateBriefRequest;
import com.netwrkly.auth.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BriefService {
    private final BriefRepository briefRepository;

    @Transactional(readOnly = true)
    public List<Brief> getBriefsByCreator(User creator) {
        log.debug("Fetching briefs for creator: {}", creator.getEmail());
        return briefRepository.findByCreatorOrderByCreatedAtDesc(creator);
    }

    @Transactional(readOnly = true)
    public List<Brief> getBriefsByBrand(User brand) {
        log.debug("Fetching briefs for brand: {}", brand.getEmail());
        return briefRepository.findByBrand(brand);
    }

    @Transactional
    public Brief createBrief(CreateBriefRequest request, User user) {
        log.debug("Creating new brief with title: {} for creator: {}", request.getTitle(), user.getEmail());
        try {
            Brief brief = new Brief();
            brief.setTitle(request.getTitle());
            brief.setBackground(request.getBackground());
            brief.setAsk(request.getAsk());
            brief.setDeliverables(request.getDeliverables());
            brief.setBudget(request.getBudget());
            brief.setCreator(user);
            brief.setBrand(user);
            brief.setStatus(BriefStatus.DRAFT); // Set initial status
            
            Brief savedBrief = briefRepository.save(brief);
            log.debug("Successfully created brief with id: {}", savedBrief.getId());
            return savedBrief;
        } catch (Exception e) {
            log.error("Error creating brief: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create brief: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Brief updateBriefStatus(Long briefId, BriefStatus newStatus, User user) {
        log.debug("Updating brief {} status to {} for user {}", briefId, newStatus, user.getEmail());
        Brief brief = briefRepository.findById(briefId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brief not found"));

        // Check if the user is the creator of the brief
        if (!brief.getCreator().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this brief");
        }

        brief.setStatus(newStatus);
        Brief updatedBrief = briefRepository.save(brief);
        log.debug("Successfully updated brief {} status to {}", briefId, newStatus);
        return updatedBrief;
    }

    @Transactional(readOnly = true)
    public List<Brief> getAllPublicBriefs() {
        log.debug("Fetching all public briefs");
        return briefRepository.findByStatus(BriefStatus.PUBLIC);
    }
} 