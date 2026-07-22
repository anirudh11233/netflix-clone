package com.saian.netflixclone.controller;

import com.saian.netflixclone.exception.FileStorageException;
import com.saian.netflixclone.service.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final StorageService storageService;

    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Admin uploads a video or poster image.
     * type=video  -> only video/* allowed
     * type=image  -> only image/* allowed
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "video") String type) {

        String allowedPrefix = switch (type.toLowerCase()) {
            case "video" -> "video/";
            case "image" -> "image/";
            default -> throw new FileStorageException("Unknown type '" + type + "'. Use 'video' or 'image'.");
        };

        String relativePath = storageService.store(file, allowedPrefix);

        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/")
                .path(relativePath)
                .toUriString();

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "path", relativePath,
                "url", url));
    }
}
