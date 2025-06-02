package com.brasfi.platforma.controller;

import com.brasfi.platforma.dto.TrilhaFormDTO;
import com.brasfi.platforma.model.EixoTematico;
import com.brasfi.platforma.model.Trilha;
import com.brasfi.platforma.service.TrilhaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import com.brasfi.platforma.dto.TrilhaFormDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Controller
@RequestMapping("/trilhas")
public class TrilhaController {

    @Autowired
    private TrilhaService trilhaService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Exibe o formulário de registro
    @GetMapping("/registrar")
    public String mostrarRegistroTrilhaForm(Model model) {
        model.addAttribute("trilha", new TrilhaFormDTO());
        return "trilha/registrarTrilha"; // Página do formulário
    }

    // Processa o envio do formulário
    @PostMapping("/registrar")
    public String registrarTrilha(
            @ModelAttribute TrilhaFormDTO trilhaFormDTO
    ) throws IOException {
        // Converte DTO para entidade
        Trilha trilha = new Trilha();
        trilha.setTitulo(trilhaFormDTO.getTitulo());
        trilha.setDescricao(trilhaFormDTO.getDescricao());
        trilha.setDuracao(trilhaFormDTO.getDuracaoHoras() + trilhaFormDTO.getDuracaoMinutos() / 60.0);
        trilha.setTopicosDeAprendizado(trilhaFormDTO.getTopicosDeAprendizado());
        trilha.setEixoTematico(EixoTematico.valueOf(trilhaFormDTO.getEixoTematico()));

        // Salvar arquivo
        MultipartFile capaFile = trilhaFormDTO.getCapaFile();
        if (capaFile != null && !capaFile.isEmpty()) {
            String nomeArquivo = System.currentTimeMillis() + "_" + capaFile.getOriginalFilename();

            // monta o path absoluto: {uploadDir}/{nomeArquivo}
            Path pastaUploads = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(pastaUploads);

            Path destino = pastaUploads.resolve(nomeArquivo);
            capaFile.transferTo(destino.toFile());

            trilha.setCapa("/" + uploadDir + "/" + nomeArquivo);
        }

        trilhaService.salvarTrilha(trilha);
        System.out.println("Trilha salva com ID: " + trilha.getId());
        return "redirect:/trilhas/listar";
    }

    @GetMapping("/deletar")
    public String mostrarConfirmacao(@RequestParam("id") Long id, Model model) {
        Trilha trilha = trilhaService.buscarPorId(id);
        model.addAttribute("trilha", trilha);
        return "trilha/deletarTrilha";
    }

    @PostMapping("/deletar")
    public String deletarTrilha(Trilha trilha) {
        trilhaService.deletarTrilha(trilha);
        return "redirect:/";
    }

    @GetMapping("/editar")
    public String mostrarEditarTrilhaForm(@RequestParam("id") Long id, Model model) {
        Trilha trilha = trilhaService.buscarPorId(id);
        model.addAttribute("trilha", trilha);
        return "trilha/editarTrilha";
    }

    @PostMapping("/editar")
    public String editarTrilha(Trilha trilha) {
        trilhaService.atualizarTrilha(trilha);
        return "redirect:/";
    }

    @GetMapping("/listar")
    public String mostrarListaTrilha(Model model) {
        List<Trilha> trilhas = trilhaService.listaTrilhas();
        model.addAttribute("trilhas", trilhas);
        return "trilha/listarTrilha";
    }

    @GetMapping("/{id}")
    public String mostrarDetalhesTrilha(@PathVariable Long id, Model model) {
        Trilha trilha = trilhaService.buscarPorId(id);
        if (trilha == null) {
            return "erro/404";
        }
        model.addAttribute("trilha", trilha);
        model.addAttribute("aulas", trilha.getAulas());
        return "trilha/detalheTrilha";
    }
}