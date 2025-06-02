package com.brasfi.platforma.controller;

import com.brasfi.platforma.model.Aula;
import com.brasfi.platforma.service.AulaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/aulas")
@RequiredArgsConstructor
public class AulaController {
    private final AulaService aulaService;

    @GetMapping("/enviar")
    public String mostrarFormularioEnvio(Model model) {
        model.addAttribute("aula", new Aula());
        return "enviar-aula";
    }

    @PostMapping("/salvar")
    public String salvarAula(@ModelAttribute("aula") Aula aula, Model model) {
        aulaService.salvarAula(aula);
        model.addAttribute("mensagem", "Aula enviada com sucesso!");
        model.addAttribute("aula", new Aula()); // limpa o formulário
        return "enviar-aula";
    }
    @GetMapping("/{id}")
    public String mostrarAula(@PathVariable Long id, Model model) {
        Aula aula = aulaService.buscarPorId(id);
        if (aula == null) {
            return "erro/404";
        }
        model.addAttribute("aula", aula);
        return "detalhe-aula";
    }

    @GetMapping("/detalhe/{id}")
    public String mostrarDetalhesAula(@PathVariable Long id, Model model) {
        Aula aula = aulaService.buscarPorId(id);
        if (aula == null) {
            throw new IllegalArgumentException("Aula não encontrada com ID: " + id);
        }

        // Extrai código do vídeo de URLs curtas
        String videoUrl = aula.getLink();
        String videoCode = "";

        if (videoUrl.contains("youtu.be/")) {
            videoCode = videoUrl.split("youtu.be/")[1].split("\\?")[0];
        } else if (videoUrl.contains("watch?v=")) {
            videoCode = videoUrl.split("v=")[1].split("&")[0];
        }

        model.addAttribute("aula", aula);
        model.addAttribute("videoCode", videoCode);

        return "detalhe-aula";
    }
}