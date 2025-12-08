package com.tally.service;

import com.tally.entity.User;
import com.tally.repository.UserRepository;
import com.tally.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * User Service Implementation
 * Handles user management operations
 * Implements IUserService interface following Dependency Inversion Principle (DIP)
 */
@Service
public class UserService implements IUserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // Constructor injection - follows Dependency Inversion Principle
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User createUser(String username, String email, Long licenceNo, String password, String fullName) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setLicenceNo(licenceNo);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setRole("USER");
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public Optional<User> authenticateUser(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && validatePassword(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }
    
    public void updateDeviceToken(Long userId, String deviceToken) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setDeviceToken(deviceToken);
            user.setDeviceLoginAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }
    
    public void clearDeviceToken(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setDeviceToken(null);
            user.setDeviceLoginAt(null);
            userRepository.save(user);
        }
    }
    
    @Override
    public String getDeviceToken(Long userId) {
        return userRepository.findById(userId)
                .map(User::getDeviceToken)
                .orElse(null);
    }
}
