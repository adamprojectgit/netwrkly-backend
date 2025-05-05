package com.netwrkly.auth.validation;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class PasswordPolicyValidator {
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[A-Z]).{8,}$"
    );
    
    public void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(
                "Password must be at least 8 characters long and contain at least one digit and one uppercase letter"
            );
        }
    }
} 