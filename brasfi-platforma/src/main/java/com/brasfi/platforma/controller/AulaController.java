package com.brasfi.platforma.controller;

import com.brasfi.platforma.model.Aula;
import com.brasfi.platforma.service.AulaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String salvarAula(@ModelAttribute Aula aula) {
        aulaService.salvarAula(aula);
        return "enviar-aula";
    }// fazer um popup em js

    /*@GetMapping("/listar")
    public String listarAulas(Model model) {
        model.addAttribute("aulas", aulaService.listarTodosAulas());
        return "listar-aulas";
    }*/
}