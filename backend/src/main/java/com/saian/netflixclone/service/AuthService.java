package com.saian.netflixclone.service;

import com.saian.netflixclone.config.JwtService;
import com.saian.netflixclone.dto.Mapper;
import com.saian.netflixclone.dto.request.LoginRequest;
import com.saian.netflixclone.dto.request.SignupRequest;
import com.saian.netflixclone.dto.response.AuthResponse;
import com.saian.netflixclone.dto.response.UserResponse;
import com.saian.netflixclone.entity.User;
import com.saian.netflixclone.enums.Role;
import com.saian.netflixclone.exception.DuplicateResourceException;
import com.saian.netflixclone.exception.InvalidCredentialsException;
import com.saian.netflixclone.exception.InvalidTokenException;
import com.saian.netflixclone.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new InvalidCredentialsException("Please verify your email before logging in");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, Mapper.toUserResponse(user));
    }

    @Transactional
    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .enabled(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationTokenExpiry(Instant.now().plus(24, ChronoUnit.HOURS))
                .build();

        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());
        return Mapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        if (user.getVerificationTokenExpiry().isBefore(Instant.now())) {
            throw new InvalidTokenException("Verification token has expired, please sign up again");
        }

        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        return Mapper.toUserResponse(user);
    }
}
