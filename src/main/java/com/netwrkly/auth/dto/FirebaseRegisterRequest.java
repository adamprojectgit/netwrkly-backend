package com.netwrkly.auth.dto;

import lombok.Data;

@Data
public class FirebaseRegisterRequest {
    private String email;
    private String firebaseUid;
    private String role;
} 