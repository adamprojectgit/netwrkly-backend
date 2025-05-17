package com.netwrkly.auth.service;

import com.netwrkly.auth.model.User;
import com.netwrkly.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;

@Service
@Primary
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        // For Firebase users, we don't need to check the password
        if (user.getFirebaseUid() != null) {
            return user;
        }
        
        // For non-Firebase users (if any), ensure password exists
        if (user.getPassword() == null) {
            throw new UsernameNotFoundException("Invalid user credentials");
        }
        
        return user;
    }
} 