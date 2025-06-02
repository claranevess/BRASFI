package com.brasfi.platforma.controller;

import com.brasfi.platforma.model.Aula;
import com.brasfi.platforma.model.Material;
import com.brasfi.platforma.repository.MaterialRepository;
import com.brasfi.platforma.service.AulaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;


@Controller
@RequestMapping("/aulas")
@RequiredArgsConstructor
public class AulaController {
    @Autowired
    private MaterialRepository materialRepository;

    private final AulaService aulaService;

    @GetMapping("/enviar")
    public String mostrarFormularioEnvio(Model model) {
        model.addAttribute("aula", new Aula());
        return "enviar-aula";
    }

    @PostMapping("/salvar")
    public String salvarAula(
            @ModelAttribute("aula") Aula aula,
            @RequestParam(value = "documentos", required = false) MultipartFile[] documentos,
            @RequestParam(value = "linkApoio", required = false) String[] linksApoio,
            Model model
    ) {
        // 1. Salvar a aula
        Aula aulaSalva = aulaService.salvarAula(aula);

        // 2. Salvar cada link de apoio (se existirem)
        if (linksApoio != null) {
            for (String link : linksApoio) {
                if (link != null && !link.isEmpty()) {
                    Material linkMaterial = new Material();
                    linkMaterial.setLinkApoio(link);
                    linkMaterial.setAula(aulaSalva);
                    materialRepository.save(linkMaterial);
                }
            }
        }

        // 3. Salvar documentos (se houver)
        if (documentos != null) {
            for (MultipartFile arquivo : documentos) {
                if (arquivo != null && !arquivo.isEmpty()) {
                    try {
                        String nomeOriginal = arquivo.getOriginalFilename();
                        String nomeUnico = System.currentTimeMillis() + "_" + nomeOriginal;

                        // Caminho absoluto para a pasta 'uploads'
                        File pastaUploads = new File("uploads");
                        if (!pastaUploads.exists()) {
                            pastaUploads.mkdirs();
                        }

                        File destino = new File(pastaUploads, nomeUnico);
                        arquivo.transferTo(destino);

                        // Salva info no banco
                        Material material = new Material();
                        material.setNomeOriginal(nomeOriginal);
                        material.setCaminhoArquivo(nomeUnico); // usado na URL
                        material.setAula(aulaSalva);
                        materialRepository.save(material);
                    } catch (IOException e) {
                        e.printStackTrace();
                        model.addAttribute("mensagem", "Erro ao salvar o arquivo: " + arquivo.getOriginalFilename());
                    }
                }
            }
        }


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

        // Extrai código do vídeo
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