package com.saian.netflixclone.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String uploadDir;

    public WebConfig(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    /** Serve files on disk (uploads/xyz.mp4) at the URL /uploads/xyz.mp4. */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        String folderName = uploadRoot.getFileName().toString();
        registry.addResourceHandler("/" + folderName + "/**")
                .addResourceLocations(uploadRoot.toUri().toString());
    }
}
