package com.brasfi.platforma.repository;

import com.brasfi.platforma.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}