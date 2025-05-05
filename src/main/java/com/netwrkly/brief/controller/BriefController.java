package com.netwrkly.brief.controller;

import com.netwrkly.brief.model.Brief;
import com.netwrkly.brief.model.BriefStatus;
import com.netwrkly.brief.service.BriefService;
import com.netwrkly.brief.dto.CreateBriefRequest;
import com.netwrkly.auth.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/briefs")
@RequiredArgsConstructor
public class BriefController {
    private final BriefService briefService;

    @GetMapping
    public ResponseEntity<List<Brief>> getBriefs(@AuthenticationPrincipal User user) {
        List<Brief> briefs = briefService.getBriefsByCreator(user);
        return ResponseEntity.ok(briefs);
    }

    @PostMapping
    public ResponseEntity<Brief> createBrief(
            @Valid @RequestBody CreateBriefRequest request,
            @AuthenticationPrincipal User user) {
        Brief brief = briefService.createBrief(request, user);
        return ResponseEntity.ok(brief);
    }

    @PatchMapping("/{briefId}/status")
    public ResponseEntity<Brief> updateBriefStatus(
            @PathVariable Long briefId,
            @RequestBody UpdateBriefStatusRequest request,
            @AuthenticationPrincipal User user) {
        Brief updatedBrief = briefService.updateBriefStatus(briefId, request.getStatus(), user);
        return ResponseEntity.ok(updatedBrief);
    }
}

record UpdateBriefStatusRequest(BriefStatus status) {
    public BriefStatus getStatus() {
        return status;
    }
} 