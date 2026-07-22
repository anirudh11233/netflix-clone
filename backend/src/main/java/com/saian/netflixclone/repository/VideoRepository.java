package com.saian.netflixclone.repository;

import com.saian.netflixclone.entity.Video;
import com.saian.netflixclone.enums.VideoCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByPublishedTrue();

    List<Video> findByPublishedTrueAndCategories(VideoCategory category);

    List<Video> findByTitleContainingIgnoreCaseAndPublishedTrue(String title);
}
