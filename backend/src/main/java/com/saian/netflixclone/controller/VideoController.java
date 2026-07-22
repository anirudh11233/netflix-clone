package com.saian.netflixclone.controller;

import com.saian.netflixclone.dto.request.VideoRequest;
import com.saian.netflixclone.dto.response.VideoResponse;
import com.saian.netflixclone.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    // ---------- Admin-only catalog management ----------

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<VideoResponse> create(@Valid @RequestBody VideoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(videoService.create(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<VideoResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody VideoRequest request) {
        return ResponseEntity.ok(videoService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        videoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** Full catalog including unpublished drafts. */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<VideoResponse>> findAll() {
        return ResponseEntity.ok(videoService.findAll());
    }

    // ---------- Any logged-in user ----------

    @GetMapping("/published")
    public ResponseEntity<List<VideoResponse>> findPublished() {
        return ResponseEntity.ok(videoService.findPublished());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.findById(id));
    }
}
