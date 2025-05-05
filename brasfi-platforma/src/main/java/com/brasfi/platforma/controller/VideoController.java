package com.brasfi.platforma.controller;

import com.brasfi.platforma.model.Video;
import com.brasfi.platforma.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

    @GetMapping("/enviar")
    public String mostrarFormularioEnvio(Model model) {
        model.addAttribute("video", new Video());
        return "enviar-video";
    }

    @PostMapping("/salvar")
    public String salvarVideo(@ModelAttribute Video video) {
        videoService.salvarVideo(video);
        return "redirect:/videos/enviar?sucesso";
    }

    @GetMapping("/listar")
    public String listarVideos(Model model) {
        model.addAttribute("videos", videoService.listarTodosVideos());
        return "listar-videos";
    }
}