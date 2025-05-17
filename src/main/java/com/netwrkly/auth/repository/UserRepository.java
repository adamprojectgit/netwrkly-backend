package com.netwrkly.auth.repository;

import com.netwrkly.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String token);
    Optional<User> findByResetPasswordToken(String token);
    boolean existsByEmail(String email);
    boolean existsByFirebaseUid(String firebaseUid);
    Optional<User> findByFirebaseUid(String firebaseUid);
} 