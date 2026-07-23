package com.saian.netflixclone.controller;

import com.saian.netflixclone.dto.Mapper;
import com.saian.netflixclone.dto.request.ChangePasswordRequest;
import com.saian.netflixclone.dto.response.UserResponse;
import com.saian.netflixclone.dto.response.VideoResponse;
import com.saian.netflixclone.entity.User;
import com.saian.netflixclone.exception.ResourceNotFoundException;
import com.saian.netflixclone.repository.UserRepository;
import com.saian.netflixclone.service.AuthService;
import com.saian.netflixclone.service.FavoriteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final FavoriteService favoriteService;

    public UserController(UserRepository userRepository,
                          AuthService authService,
                          FavoriteService favoriteService) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.favoriteService = favoriteService;
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(Authentication authentication,
                                                              @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    /** Who am I? — proves a JWT works. The filter put the email into Authentication. */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(Mapper.toUserResponse(user));
    }

    // ---------- Favorites (My List) — always scoped to the logged-in user ----------

    @GetMapping("/me/favorites")
    public ResponseEntity<List<VideoResponse>> getFavorites(Authentication authentication) {
        return ResponseEntity.ok(favoriteService.getFavorites(authentication.getName()));
    }

    @PostMapping("/me/favorites/{videoId}")
    public ResponseEntity<List<VideoResponse>> addFavorite(Authentication authentication,
                                                           @PathVariable Long videoId) {
        return ResponseEntity.ok(favoriteService.addFavorite(authentication.getName(), videoId));
    }

    @DeleteMapping("/me/favorites/{videoId}")
    public ResponseEntity<List<VideoResponse>> removeFavorite(Authentication authentication,
                                                              @PathVariable Long videoId) {
        return ResponseEntity.ok(favoriteService.removeFavorite(authentication.getName(), videoId));
    }
}
