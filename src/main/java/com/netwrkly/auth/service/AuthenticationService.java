package com.netwrkly.auth.service;

import com.netwrkly.auth.model.User;
import com.netwrkly.auth.repository.UserRepository;
import com.netwrkly.auth.security.RateLimiter;
import com.netwrkly.auth.validation.PasswordPolicyValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthenticationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordPolicyValidator passwordValidator;
    
    @Autowired
    private RateLimiter rateLimiter;
    
    @Transactional
    public String register(User user) {
        try {
            logger.info("Starting registration for user: {}", user.getEmail());
            
            // Validate email format
            if (!isValidEmail(user.getEmail())) {
                logger.error("Invalid email format: {}", user.getEmail());
                throw new IllegalArgumentException("Invalid email format");
            }
            
            // Validate password
            passwordValidator.validatePassword(user.getPassword());
            
            // Check if user already exists
            if (userRepository.existsByEmail(user.getEmail())) {
                logger.error("User already exists: {}", user.getEmail());
                throw new IllegalArgumentException("Email already registered");
            }
            
            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            // Generate verification token
            user.setVerificationToken(UUID.randomUUID().toString());
            
            // Save user
            User savedUser = userRepository.save(user);
            logger.info("User registered successfully: {}", savedUser.getEmail());
            
            // Send verification email
            emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getVerificationToken());
            
            return "Registration successful. Please check your email for verification.";
        } catch (Exception e) {
            logger.error("Registration failed for user: {}", user.getEmail(), e);
            throw e;
        }
    }
    
    public String login(String email, String password) {
        // Rate limiting check
        if (!rateLimiter.isAllowed(email)) {
            throw new RuntimeException("Too many login attempts. Please try again later.");
        }
        
        try {
            // Sanitize inputs
            email = email.toLowerCase().trim();
            
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (!user.isEmailVerified()) {
                throw new RuntimeException("Please verify your email before logging in");
            }
            
            if (!user.isEnabled()) {
                throw new RuntimeException("Account is disabled. Please contact support.");
            }
            
            // Reset rate limiter on successful login
            rateLimiter.resetAttempts(email);
            
            return jwtService.generateToken(user);
        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password");
        }
    }
    
    @Transactional
    public boolean verifyEmail(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Invalid verification token");
        }
        
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));
        
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
        
        return true;
    }
    
    @Transactional
    public void initiatePasswordReset(String email) {
        // Rate limiting for password reset requests
        if (!rateLimiter.isAllowed(email)) {
            throw new RuntimeException("Too many password reset attempts. Please try again later.");
        }
        
        // Sanitize input
        email = email.toLowerCase().trim();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setResetPasswordToken(UUID.randomUUID().toString());
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);
        
        emailService.sendPasswordResetEmail(user.getEmail(), user.getResetPasswordToken());
    }
    
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        if (!StringUtils.hasText(token) || !StringUtils.hasText(newPassword)) {
            throw new IllegalArgumentException("Invalid token or password");
        }
        
        // Validate new password
        passwordValidator.validatePassword(newPassword);
        
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));
        
        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);
        
        return true;
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }

    public User getCurrentUser(String token) {
        String email = jwtService.extractUsername(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
} 