package com.netwrkly.auth.service;

import com.netwrkly.auth.model.User;
import com.netwrkly.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public User registerFirebaseUser(String email, String firebaseUid, User.Role role) {
        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User with this email already exists");
        }
        
        if (userRepository.existsByFirebaseUid(firebaseUid)) {
            throw new RuntimeException("User with this Firebase UID already exists");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(email);
        user.setFirebaseUid(firebaseUid);
        user.setRole(role);
        user.setEnabled(true);
        user.setEmailVerified(false);
        // Password is not set for Firebase users
        
        return userRepository.save(user);
    }
    
    public User getCurrentUser() {
        // TODO: Implement getting current user from security context
        return null;
    }
} 