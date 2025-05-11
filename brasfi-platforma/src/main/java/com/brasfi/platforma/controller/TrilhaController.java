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


@Controller
@RequestMapping("/trilhas")
public class TrilhaController {

    @Autowired
    private TrilhaService trilhaService;

    // Rota para exibir o formulário de registro
    @GetMapping("/registrar")
    public String mostrarRegistroTrilhaForm(Model model) {
        model.addAttribute("trilha", new Trilha());
        return "registrarTrilha"; // Retorna a página de registro
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
        return "deletarTrilha";
    }

    @PostMapping("/deletar")
    public String deletarTrilha(Trilha trilha) {
        trilhaService.deletarTrilha(trilha);
        return "redirect:/";
    }
}
