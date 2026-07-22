package com.saian.netflixclone.service;

import com.saian.netflixclone.dto.Mapper;
import com.saian.netflixclone.dto.request.VideoRequest;
import com.saian.netflixclone.dto.response.VideoResponse;
import com.saian.netflixclone.entity.Video;
import com.saian.netflixclone.exception.ResourceNotFoundException;
import com.saian.netflixclone.repository.VideoRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final StorageService storageService;

    public VideoService(VideoRepository videoRepository, StorageService storageService) {
        this.videoRepository = videoRepository;
        this.storageService = storageService;
    }

    @Transactional
    public VideoResponse create(VideoRequest request) {
        Video video = Video.builder()
                .title(request.title())
                .description(request.description())
                .year(request.year())
                .rating(request.rating())
                .src(request.src())
                .poster(request.poster())
                .published(request.published())
                .categories(request.categories())
                .build();
        return Mapper.toVideoResponse(videoRepository.save(video));
    }

    @Transactional
    public VideoResponse update(Long id, VideoRequest request) {
        Video video = findVideo(id);
        video.setTitle(request.title());
        video.setDescription(request.description());
        video.setYear(request.year());
        video.setRating(request.rating());
        video.setSrc(request.src());
        video.setPoster(request.poster());
        video.setPublished(request.published());
        video.setCategories(request.categories());
        return Mapper.toVideoResponse(video);
    }

    @Transactional
    public void delete(Long id) {
        videoRepository.delete(findVideo(id));
    }

    /** Admin view — everything, drafts included. */
    public List<VideoResponse> findAll() {
        return videoRepository.findAll().stream().map(Mapper::toVideoResponse).toList();
    }

    /** What normal users browse. */
    public List<VideoResponse> findPublished() {
        return videoRepository.findByPublishedTrue().stream().map(Mapper::toVideoResponse).toList();
    }

    public VideoResponse findById(Long id) {
        return Mapper.toVideoResponse(findVideo(id));
    }

    /** Loads the actual video file on disk so the controller can stream it. */
    public Resource loadVideoResource(Long id) {
        Video video = findVideo(id);
        String src = video.getSrc();
        // src may be "uploads/xyz.mp4" or a full URL — we only need the file name.
        String filename = src.substring(src.lastIndexOf('/') + 1);
        return storageService.loadAsResource(filename);
    }

    private Video findVideo(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id " + id));
    }
}
