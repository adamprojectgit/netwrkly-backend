package com.netwrkly.auth.security;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {
    
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;
    
    private final Map<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();
    
    public boolean isAllowed(String email) {
        LoginAttempt attempt = loginAttempts.get(email);
        
        if (attempt == null) {
            attempt = new LoginAttempt();
            loginAttempts.put(email, attempt);
            return true;
        }
        
        if (attempt.isLocked()) {
            return false;
        }
        
        if (attempt.getAttempts() >= MAX_ATTEMPTS) {
            attempt.setLocked(true);
            attempt.setLockoutUntil(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
            return false;
        }
        
        attempt.incrementAttempts();
        return true;
    }
    
    public void resetAttempts(String email) {
        loginAttempts.remove(email);
    }
    
    private static class LoginAttempt {
        private int attempts = 0;
        private boolean locked = false;
        private LocalDateTime lockoutUntil;
        
        public void incrementAttempts() {
            this.attempts++;
        }
        
        public int getAttempts() {
            return attempts;
        }
        
        public boolean isLocked() {
            if (locked && LocalDateTime.now().isAfter(lockoutUntil)) {
                locked = false;
                attempts = 0;
                return false;
            }
            return locked;
        }
        
        public void setLocked(boolean locked) {
            this.locked = locked;
        }
        
        public LocalDateTime getLockoutUntil() {
            return lockoutUntil;
        }
        
        public void setLockoutUntil(LocalDateTime lockoutUntil) {
            this.lockoutUntil = lockoutUntil;
        }
    }
} 