package com.brasfi.platforma.controller;

import com.brasfi.platforma.model.Aviso; //entidade
import com.brasfi.platforma.repository.AvisoRepository; //interface do rep pra buscar/salvar os avisos
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; //bd
import org.springframework.web.bind.annotation.*; //@s necessários
import com.brasfi.platforma.model.User;
import com.brasfi.platforma.model.TipoUsuario;
import com.brasfi.platforma.service.UserService;
import org.springframework.security.core.Authentication;


@Controller
@RequestMapping("/avisos") //urls começam com avisos
public class AvisoController {

    @Autowired //faz repository funcionar
    private AvisoRepository avisoRepository; //acesso ao banco de dados pra salvar e buscar

    @Autowired
    private UserService userService;


    @GetMapping("/novo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("aviso", new Aviso());
        return "criar_Aviso"; // agora está correto!
    }

    @PostMapping("/salvar")
    public String salvarAviso(@Valid @ModelAttribute Aviso aviso, Authentication authentication) {
        avisoRepository.save(aviso);

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.getUserByUsername(username);

            if (user != null && user.getTipoUsuario() == TipoUsuario.ADMINISTRADOR) {
                return "redirect:/avisos/dashboard/adm";
            } else {
                return "redirect:/avisos/dashboard/estudante";
            }
        }

        return "redirect:/avisos";
    }


    @GetMapping("/dashboard/adm")
    public String mostrarDashboard(Model model) {
        return "visaoAdmQuadroAvisos";
    }
    @GetMapping("/dashboard/estudante")
    public String mostrarDashboardEstudante(Model model) {
        return "visaoEstQuadroAvisos";
    }
}
