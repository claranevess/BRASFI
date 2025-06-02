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
            @RequestParam("duracaoInput") String duracaoStr,
            @RequestParam("capaFile") MultipartFile capaFile
    ) throws IOException {
        // Converte "01:30" para double 1.5
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

    // Ajustado para interpretar "hh:mm"
    private double parseDuracao(String duracaoStr) {
        if (duracaoStr == null || duracaoStr.isEmpty()) {
            throw new IllegalArgumentException("O campo de duração está vazio ou nulo.");
        }
        try {
            String[] partes = duracaoStr.split(":");
            if (partes.length != 2) {
                throw new IllegalArgumentException("Formato inválido para duração. Use hh:mm.");
            }
            int horas = Integer.parseInt(partes[0]);
            int minutos = Integer.parseInt(partes[1]);

            return horas + minutos / 60.0;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Não foi possível converter duração para número: " + duracaoStr, e);
        }
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
    public String editarTrilha(
            @ModelAttribute Trilha trilha,
            @RequestParam("duracaoInput") String duracaoStr,
            @RequestParam("capaFile") MultipartFile capaFile
    ) throws IOException {
        // Converte "hh:mm" para double
        double duracao = parseDuracao(duracaoStr);
        trilha.setDuracao(duracao);

        // Se uma nova imagem for enviada, substitui
        if (capaFile != null && !capaFile.isEmpty()) {
            String nomeArquivo = System.currentTimeMillis() + "_" + capaFile.getOriginalFilename();

            Path pastaUploads = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(pastaUploads);

            Path destino = pastaUploads.resolve(nomeArquivo);
            capaFile.transferTo(destino.toFile());

            trilha.setCapa("/" + uploadDir + "/" + nomeArquivo);
        } else {
            // Preserva a capa existente se não houver nova imagem
            Trilha trilhaExistente = trilhaService.buscarPorId(trilha.getId());
            trilha.setCapa(trilhaExistente.getCapa());
        }

        trilhaService.atualizarTrilha(trilha);
        return "redirect:/trilhas/listar";
    }

    @GetMapping("/listar")
    public String mostrarListaTrilha(Model model) {
        List<Trilha> trilhas = trilhaService.listaTrilhas();
        model.addAttribute("trilhas", trilhas);
        return "trilha/listarTrilha";
    }



}
