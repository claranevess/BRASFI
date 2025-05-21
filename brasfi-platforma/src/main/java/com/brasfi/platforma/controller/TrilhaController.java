package com.brasfi.platforma.controller;

import com.brasfi.platforma.model.Trilha;
import com.brasfi.platforma.service.TrilhaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequestMapping("/trilhas")
public class TrilhaController {

    @Autowired
    private TrilhaService trilhaService;

    // Rota para exibir o formulário de registro
    @GetMapping("/registrar")
    public String mostrarRegistroTrilhaForm(Model model) {
        model.addAttribute("trilha", new Trilha());
        return "trilha/registrarTrilha"; // Retorna a página de registro
    }

    // Rota para processar o envio do formulário
    @PostMapping("/registrar")
    public String registrarTrilha(Trilha trilha) {
        trilhaService.salvarTrilha(trilha);
        return "redirect:/";
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
