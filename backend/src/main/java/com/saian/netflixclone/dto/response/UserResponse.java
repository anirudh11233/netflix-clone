package com.saian.netflixclone.dto.response;

import com.saian.netflixclone.enums.Role;

import java.time.Instant;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        Role role,
        boolean enabled,
        Instant createdAt
) {
}
