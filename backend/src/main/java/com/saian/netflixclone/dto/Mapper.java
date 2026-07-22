package com.saian.netflixclone.dto;

import com.saian.netflixclone.dto.response.UserResponse;
import com.saian.netflixclone.dto.response.VideoResponse;
import com.saian.netflixclone.entity.User;
import com.saian.netflixclone.entity.Video;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public final class Mapper {

    private Mapper() {
    }

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled(),
                user.getCreatedAt());
    }

    public static VideoResponse toVideoResponse(Video video) {
        return new VideoResponse(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getYear(),
                video.getRating(),
                absoluteUrl(video.getSrc()),
                absoluteUrl(video.getPoster()),
                video.isPublished(),
                video.getCategories(),
                video.getCreatedAt());
    }

    /** Stored paths are relative (e.g. "uploads/abc.mp4"); responses carry full URLs. */
    private static String absoluteUrl(String path) {
        if (path == null || path.isBlank() || path.startsWith("http")) {
            return path;
        }
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/")
                .path(path)
                .toUriString();
    }
}
