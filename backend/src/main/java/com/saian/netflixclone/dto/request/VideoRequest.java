package com.saian.netflixclone.dto.request;

import com.saian.netflixclone.enums.VideoCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record VideoRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        Integer year,

        Double rating,

        @NotBlank(message = "Video source is required")
        String src,

        String poster,

        boolean published,

        @NotEmpty(message = "At least one category is required")
        Set<VideoCategory> categories
) {
}
