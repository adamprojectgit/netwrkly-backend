package com.netwrkly.auth.controller;

import com.netwrkly.auth.dto.FirebaseRegisterRequest;
import com.netwrkly.auth.model.User;
import com.netwrkly.auth.service.AuthenticationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationService authService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody FirebaseRegisterRequest request) {
        try {
            // 1. Verify the Firebase ID token
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Missing or invalid Authorization header");
            }
            String idToken = bearerToken.substring(7);
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);

            // 2. Extract UID and email from token
            String tokenUid = decoded.getUid();
            String tokenEmail = decoded.getEmail();

            // 3. Ensure UID and email match the request body
            if (!tokenUid.equals(request.getFirebaseUid()) || !tokenEmail.equals(request.getEmail())) {
                return ResponseEntity.status(403).body("Token UID/email does not match request body");
            }

            // 4. Register user in our database
            User user = authService.registerFirebaseUser(
                request.getEmail(),
                request.getFirebaseUid(),
                User.Role.valueOf(request.getRole())
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("user", new UserResponse(user));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            boolean verified = authService.verifyEmail(token);
            return ResponseEntity.ok("Email verified successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            authService.initiatePasswordReset(email);
            return ResponseEntity.ok("If your email is registered, you will receive a password reset link");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam String token,
            @RequestBody ResetPasswordRequest request) {
        try {
            boolean reset = authService.resetPassword(token, request.getNewPassword());
            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            User user = authService.getCurrentUser(token);
            return ResponseEntity.ok(new UserResponse(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    private static class LoginRequest {
        private String email;
        private String password;
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
    
    private static class LoginResponse {
        private String token;
        
        public LoginResponse(String token) {
            this.token = token;
        }
        
        public String getToken() {
            return token;
        }
        
        public void setToken(String token) {
            this.token = token;
        }
    }
    
    private static class ResetPasswordRequest {
        private String newPassword;
        
        public String getNewPassword() {
            return newPassword;
        }
        
        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
    
    private static class UserResponse {
        private Long id;
        private String email;
        private String role;
        private boolean emailVerified;
        private String createdAt;

        public UserResponse(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.role = user.getRole().name();
            this.emailVerified = user.isEmailVerified();
            this.createdAt = user.getCreatedAt().toString();
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public boolean isEmailVerified() { return emailVerified; }
        public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }
} 