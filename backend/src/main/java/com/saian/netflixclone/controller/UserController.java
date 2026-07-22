package com.saian.netflixclone.controller;

import com.saian.netflixclone.dto.Mapper;
import com.saian.netflixclone.dto.response.UserResponse;
import com.saian.netflixclone.entity.User;
import com.saian.netflixclone.exception.ResourceNotFoundException;
import com.saian.netflixclone.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Who am I? — proves a JWT works. The filter put the email into Authentication. */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(Mapper.toUserResponse(user));
    }
}
