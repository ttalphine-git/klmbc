package com.classifiedads.service;

import com.classifiedads.model.dto.AuthResponse;
import com.classifiedads.model.dto.UserRegistrationRequest;
import com.classifiedads.model.entity.User;
import com.classifiedads.repository.UserRepository;
import com.classifiedads.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public AuthResponse register(UserRegistrationRequest request, MultipartFile profileImage) throws IOException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .bio(request.getBio())
                .isVerified(false)
                .isActive(true)
                .build();

        if (profileImage != null && !profileImage.isEmpty()) {
            String filePath = fileStorageService.uploadFile(profileImage, "users/" + user.getId());
            user.setProfileImageUrl(filePath);
        }

        User savedUser = userRepository.save(user);

        if (profileImage != null && !profileImage.isEmpty()) {
            String filePath = fileStorageService.uploadFile(profileImage, "users/" + savedUser.getId());
            savedUser.setProfileImageUrl(filePath);
            savedUser = userRepository.save(savedUser);
        }

        String token = jwtTokenProvider.generateTokenFromEmail(savedUser.getEmail());

        return AuthResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .message("User registered successfully")
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            String token = jwtTokenProvider.generateToken(authentication);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return AuthResponse.builder()
                    .token(token)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .message("Login successful")
                    .build();
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            throw new RuntimeException("Invalid email or password");
        }
    }

    @Transactional(readOnly = true)
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
}
