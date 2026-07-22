package com.saian.netflixclone.entity;

import com.saian.netflixclone.enums.VideoCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "videos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private Integer year;

    private Double rating;

    @Column(nullable = false)
    private String src;

    private String poster;

    @Column(nullable = false)
    private boolean published;

    @ElementCollection(targetClass = VideoCategory.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "video_categories", joinColumns = @JoinColumn(name = "video_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    @Builder.Default
    private Set<VideoCategory> categories = new HashSet<>();

    @ManyToMany(mappedBy = "favorites", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<User> favoritedBy = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
