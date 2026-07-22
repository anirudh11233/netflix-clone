package com.saian.netflixclone.controller;

import com.saian.netflixclone.dto.request.VideoRequest;
import com.saian.netflixclone.dto.response.VideoResponse;
import com.saian.netflixclone.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    // ---------- Streaming (supports HTTP Range so the player can seek) ----------

    /** How many bytes to send per chunk when the browser requests a range. */
    private static final long CHUNK_SIZE = 1_000_000; // 1 MB

    @GetMapping("/{id}/stream")
    public ResponseEntity<ResourceRegion> stream(@PathVariable Long id,
                                                 @RequestHeader HttpHeaders headers) throws IOException {
        Resource video = videoService.loadVideoResource(id);
        long total = video.contentLength();

        List<HttpRange> ranges = headers.getRange();
        ResourceRegion region;
        HttpStatus status;

        if (ranges.isEmpty()) {
            // No Range header — send the start of the file with a 200.
            region = new ResourceRegion(video, 0, Math.min(CHUNK_SIZE, total));
            status = HttpStatus.OK;
        } else {
            // The player asked for a byte range — return just that slice with a 206.
            HttpRange range = ranges.get(0);
            long start = range.getRangeStart(total);
            long end = range.getRangeEnd(total);
            region = new ResourceRegion(video, start, Math.min(CHUNK_SIZE, end - start + 1));
            status = HttpStatus.PARTIAL_CONTENT;
        }

        MediaType contentType = MediaTypeFactory.getMediaType(video)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.status(status)
                .contentType(contentType)
                .body(region);
    }
}
