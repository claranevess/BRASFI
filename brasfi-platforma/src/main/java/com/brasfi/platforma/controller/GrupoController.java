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
import com.brasfi.platforma.service.UserService; // Importe o UserService

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/grupo")
public class GrupoController {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private UserService userService; // Injete o UserService AQUI

    @GetMapping("/listar")
    public String listarGrupos(Model model){
        // Obter o usuário logado e adicionar ao modelo
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        List<Grupo> myGroups = new ArrayList<>(); // Initialize to empty lists
        List<Grupo> exploreGroups = new ArrayList<>();

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) { // Check for anonymousUser
            String username = auth.getName();
            currentUser = userService.getUserByUsername(username);
            model.addAttribute("currentUser", currentUser);

            if (currentUser != null) {
                // Fetch groups the current user is a part of
                myGroups = grupoService.findGruposByUserId(currentUser.getId());
                // Fetch groups the current user is NOT a part of
                exploreGroups = grupoService.findGruposNotJoinedByUserId(currentUser.getId());
            }
        } else {
            // If no user is logged in, show all groups in "Explore" or leave both empty
            // For now, let's put all groups in explore if no user is logged in
            exploreGroups = grupoRepository.findAll();
            // Or you could keep both empty if you want a stricter view for unauthenticated users
            // myGroups = new ArrayList<>();
            // exploreGroups = new ArrayList<>();
        }
        model.addAttribute("myGroups", myGroups); // Now correctly named for Thymeleaf
        model.addAttribute("exploreGroups", exploreGroups); // Now correctly named for Thymeleaf

        return "listar_grupos";
    }

    @GetMapping("/criar_grupo")
    public String criarGrupo(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        // Use o userService para buscar o criador
        User criador = userService.getUserByUsername(username);


        if (criador.getTipoUsuario() != TipoUsuario.MENTOR) {
            throw new AccessDeniedException("Apenas mentores podem criar grupos.");
        }

        model.addAttribute("grupo", new Grupo());
        return "criar_grupo";
    }

    @PostMapping("/criar_grupo")
    public String salvarGrupo(@ModelAttribute Grupo grupo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User criador = userService.getUserByUsername(username); // Use userService for creator

        if (criador == null || criador.getTipoUsuario() != TipoUsuario.MENTOR) { // Added null check for criador
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