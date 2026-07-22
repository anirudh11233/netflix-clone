package com.saian.netflixclone.dto.response;

public record AuthResponse(
        String token,
        UserResponse user
) {
}
