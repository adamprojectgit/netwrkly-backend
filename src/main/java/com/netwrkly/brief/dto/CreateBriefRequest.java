package com.netwrkly.brief.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CreateBriefRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Background is required")
    private String background;

    @NotBlank(message = "Ask description is required")
    private String ask;

    @NotBlank(message = "Deliverables are required")
    private String deliverables;

    @NotBlank(message = "Budget is required")
    private String budget;
} 