package com.brasfi.platforma.controller;

import com.brasfi.platforma.model.Trilha;
import com.brasfi.platforma.service.TrilhaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

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

    // Exibe o formulário
    @GetMapping("/registrar")
    public String mostrarRegistroTrilhaForm(Model model) {
        model.addAttribute("trilha", new Trilha());
        return "trilha/registrarTrilha";
    }

    // Processa o envio
    @PostMapping("/registrar")
    public String registrarTrilha(
            @ModelAttribute Trilha trilha,
            @RequestParam("duracaoInput") String duracaoStr, // ex: "1h 30min"
            @RequestParam("capaFile") MultipartFile capaFile
    ) throws IOException {
        // Converte "1h 30min" em double: 1.5
        double duracao = parseDuracao(duracaoStr);
        trilha.setDuracao(duracao);

        // Salva arquivo
        if (capaFile != null && !capaFile.isEmpty()) {
            String nomeArquivo = System.currentTimeMillis() + "_" + capaFile.getOriginalFilename();

            Path pastaUploads = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(pastaUploads);

            Path destino = pastaUploads.resolve(nomeArquivo);
            capaFile.transferTo(destino.toFile());

            trilha.setCapa("/" + uploadDir + "/" + nomeArquivo);
        }

        trilhaService.salvarTrilha(trilha);
        return "redirect:/trilhas/listar";
    }

    // Auxiliar para converter string "1h 30min" -> double 1.5
    private double parseDuracao(String duracaoStr) {
        int horas = 0;
        int minutos = 0;

        if (duracaoStr != null && !duracaoStr.isEmpty()) {
            String[] partes = duracaoStr.split(" ");
            for (String parte : partes) {
                if (parte.endsWith("h")) {
                    horas = Integer.parseInt(parte.replace("h", ""));
                } else if (parte.endsWith("min")) {
                    minutos = Integer.parseInt(parte.replace("min", ""));
                }
            }
        }

        return horas + minutos / 60.0;
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



}
