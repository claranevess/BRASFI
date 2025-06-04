package com.brasfi.platforma.controller;

import com.brasfi.platforma.model.Aula;
import com.brasfi.platforma.model.Material;
import com.brasfi.platforma.repository.MaterialRepository;
import com.brasfi.platforma.service.AulaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public String salvarAula(@ModelAttribute Aula aula,
                             @RequestParam("documentos") MultipartFile[] documentos,
                             @RequestParam(value = "linkApoio", required = false) String[] linksApoio,
                             Model model) {

        Aula aulaSalva = aulaService.salvarAula(aula);

        // Salva arquivos
        if (documentos != null) {
            System.out.println(">>> Total de arquivos recebidos: " + documentos.length);
            for (MultipartFile arquivo : documentos) {
                if (arquivo != null && !arquivo.isEmpty()) {
                    try {
                        String nomeOriginal = arquivo.getOriginalFilename();
                        String caminhoPasta = "uploads/";
                        File pastaUploads = new File(caminhoPasta);
                        if (!pastaUploads.exists()) {
                            pastaUploads.mkdirs();
                        }

                        String caminhoCompleto = caminhoPasta + nomeOriginal;
                        arquivo.transferTo(new File(caminhoCompleto));

                        Material material = new Material();
                        material.setNomeOriginal(nomeOriginal);
                        material.setCaminhoArquivo(nomeOriginal); // apenas nome, pois /uploads/ já é servido
                        material.setAula(aulaSalva);

                        materialRepository.save(material);
                        System.out.println(">>> Arquivo salvo: " + nomeOriginal);

                    } catch (IOException e) {
                        e.printStackTrace();
                        model.addAttribute("mensagem", "Erro ao salvar o arquivo: " + arquivo.getOriginalFilename());
                    }
                }
            }
        }

        // Salva links
        if (linksApoio != null) {
            for (String link : linksApoio) {
                if (link != null && !link.isBlank()) {
                    Material material = new Material();
                    material.setLinkApoio(link);
                    material.setAula(aulaSalva);
                    materialRepository.save(material);
                    System.out.println(">>> Link salvo: " + link);
                }
            }
        }

        model.addAttribute("mensagem", "Aula salva com sucesso!");
        return "redirect:/aulas/detalhe/" + aulaSalva.getId();
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

    @PatchMapping("/{id}/concluir")
    @ResponseBody
    public ResponseEntity<String> concluirAula(@PathVariable Long id) {
        boolean sucesso = aulaService.marcarComoConcluida(id);
        if (sucesso) {
            return ResponseEntity.ok("Aula concluída com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aula não encontrada.");
        }
    }
}