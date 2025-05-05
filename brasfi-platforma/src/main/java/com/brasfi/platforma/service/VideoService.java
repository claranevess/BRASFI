package com.brasfi.platforma.service;

import com.brasfi.platforma.model.Video;
import com.brasfi.platforma.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;

    public Video salvarVideo(Video video) {
        return videoRepository.save(video);
    }

    public List<Video> listarTodosVideos() {
        return videoRepository.findAll();
    }
}