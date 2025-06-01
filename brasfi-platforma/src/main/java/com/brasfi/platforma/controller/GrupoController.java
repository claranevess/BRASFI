package com.brasfi.platforma.controller;

import com.brasfi.platforma.model.Grupo;
import com.brasfi.platforma.repository.GrupoRepository;
import com.brasfi.platforma.service.GrupoService;
import org.springframework.beans.factory.annotation.Autowired;
import com.brasfi.platforma.model.TipoUsuario;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import com.brasfi.platforma.model.User;

import java.util.List;

@Controller
@RequestMapping("/grupo")
public class GrupoController {

    @Autowired
    private GrupoRepository grupoRepository;

   @Autowired
   private GrupoService grupoService;

    @GetMapping("/listar")
    public String listarGrupos(Model model){
        List<Grupo> grupos = grupoRepository.findAll();
        model.addAttribute("grupos", grupos);
        return "listar_grupos";
    }

    @GetMapping("/criar_grupo")
    public String criarGrupo(Model model) {
        model.addAttribute("grupo", new Grupo());
        return "criar_grupo";
    }

    @PostMapping("/criar_grupo")
    public String salvarGrupo(@ModelAttribute Grupo grupo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();  // isso retorna o nome de login, como "nina2"
        User criador = grupoService.getUserByUsername(username);


        if (criador.getTipoUsuario() != TipoUsuario.MENTOR) {
            throw new AccessDeniedException("Apenas mentores podem criar grupos.");
        }

        grupoService.salvarComCriador(grupo, criador);
        return "redirect:/grupo/listar";
    }


    @PostMapping("/entrar/{id}")
    public String entrarGrupo(@PathVariable("id") Long grupoId, @RequestParam("usuarioId") Long usuarioId) {
        grupoService.entrarGrupo(grupoId, usuarioId);
        return "redirect:/grupo/listar";
    }
}