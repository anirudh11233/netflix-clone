package com.saian.netflixclone.service;

import com.saian.netflixclone.exception.FileStorageException;
import com.saian.netflixclone.exception.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService {

    private final Path uploadRoot;

    public StorageService(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    /** Make sure the uploads/ folder exists the moment the app starts. */
    @PostConstruct
    void init() {
        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException e) {
            throw new FileStorageException("Could not create the upload directory", e);
        }
    }

    /**
     * Saves an uploaded file to disk under a random name.
     *
     * @param file          the incoming multipart file
     * @param allowedPrefix content-type that's allowed, e.g. "video/" or "image/"
     * @return the relative path to store, e.g. "uploads/9af3....mp4"
     */
    public String store(MultipartFile file, String allowedPrefix) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("No file was uploaded (the file is empty)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith(allowedPrefix)) {
            throw new FileStorageException(
                    "Invalid file type '" + contentType + "'. Expected a " + allowedPrefix + "* file.");
        }

        String original = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String extension = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            extension = original.substring(dot);
        }

        String storedName = UUID.randomUUID() + extension;
        Path target = uploadRoot.resolve(storedName).normalize();

        // Safety: the resolved path must stay inside the uploads folder.
        if (!target.getParent().equals(uploadRoot)) {
            throw new FileStorageException("Cannot store file outside the upload directory");
        }

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Failed to save the uploaded file", e);
        }

        // Relative path — Mapper turns this into a full URL in responses.
        return uploadRoot.getFileName() + "/" + storedName;
    }

    /**
     * Loads a previously stored file as a readable Resource for streaming.
     *
     * @param filename just the file name (e.g. "9af3...c2.mp4"), not a full path or URL
     */
    public Resource loadAsResource(String filename) {
        Path file = uploadRoot.resolve(filename).normalize();

        // Safety: never let a crafted name escape the uploads folder.
        if (!file.getParent().equals(uploadRoot)) {
            throw new FileStorageException("Invalid file path: " + filename);
        }

        try {
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("File not found: " + filename);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new FileStorageException("Could not read file: " + filename, e);
        }
    }
}
