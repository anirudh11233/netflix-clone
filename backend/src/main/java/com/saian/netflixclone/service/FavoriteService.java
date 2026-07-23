package com.saian.netflixclone.service;

import com.saian.netflixclone.dto.Mapper;
import com.saian.netflixclone.dto.response.VideoResponse;
import com.saian.netflixclone.entity.User;
import com.saian.netflixclone.entity.Video;
import com.saian.netflixclone.exception.ResourceNotFoundException;
import com.saian.netflixclone.repository.UserRepository;
import com.saian.netflixclone.repository.VideoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    public FavoriteService(UserRepository userRepository, VideoRepository videoRepository) {
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
    }

    /** Add a video to the current user's list. Adding an existing one is a no-op (it's a Set). */
    @Transactional
    public List<VideoResponse> addFavorite(String email, Long videoId) {
        User user = getUser(email);
        Video video = getVideo(videoId);
        user.getFavorites().add(video);
        return toResponseList(user);
    }

    @Transactional
    public List<VideoResponse> removeFavorite(String email, Long videoId) {
        User user = getUser(email);
        Video video = getVideo(videoId);
        user.getFavorites().remove(video);
        return toResponseList(user);
    }

    @Transactional(readOnly = true)
    public List<VideoResponse> getFavorites(String email) {
        return toResponseList(getUser(email));
    }

    private List<VideoResponse> toResponseList(User user) {
        return user.getFavorites().stream().map(Mapper::toVideoResponse).toList();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Video getVideo(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id " + id));
    }
}
