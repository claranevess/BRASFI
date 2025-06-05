package com.brasfi.platforma.controller;

import com.brasfi.platforma.model.Aviso; //entidade
import com.brasfi.platforma.repository.AvisoRepository; //interface do rep pra buscar/salvar os avisos
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; //bd
import org.springframework.web.bind.annotation.*; //@s necessários

@Controller
@RequestMapping("/avisos") //urls começam com avisos
public class AvisoController {

    @Autowired //faz repository funcionar
    private AvisoRepository avisoRepository; //acesso ao banco de dados pra salvar e buscar

    @GetMapping("/dashboard/adm")
    public String mostrarDashboard(Model model) {
        return "visaoAdmQuadroAvisos";
    }
    @GetMapping("/dashboard/estudante")
    public String mostrarDashboardEstudante(Model model) {
        return "visaoEstQuadroAvisos";
    }
}
