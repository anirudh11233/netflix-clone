package com.saian.netflixclone.dto.response;

import com.saian.netflixclone.enums.VideoCategory;

import java.time.Instant;
import java.util.Set;

public record VideoResponse(
        Long id,
        String title,
        String description,
        Integer year,
        Double rating,
        String src,
        String poster,
        boolean published,
        Set<VideoCategory> categories,
        Instant createdAt
) {
}
