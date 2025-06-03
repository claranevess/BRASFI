// com.brasfi.platforma.controller.GrupoController.java
package com.brasfi.platforma.controller;

import com.brasfi.platforma.dto.MemberDto;
import com.brasfi.platforma.model.Grupo;
import com.brasfi.platforma.model.User;
import com.brasfi.platforma.model.TipoUsuario;
import com.brasfi.platforma.model.SolicitacaoAcesso; // Import new entity
import com.brasfi.platforma.repository.GrupoRepository;
import com.brasfi.platforma.service.GrupoService;
import com.brasfi.platforma.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity; // For @ResponseBody / ResponseEntity
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/grupo")
public class GrupoController {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private UserService userService;

    @GetMapping("/listar")
    public String listarGrupos(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        List<Grupo> myGroups = new ArrayList<>();
        List<Grupo> exploreGroups = new ArrayList<>();
        List<SolicitacaoAcesso> pendingSolicitacoes = new ArrayList<>(); // New list for admin view

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = auth.getName();
            currentUser = userService.getUserByUsername(username);
            model.addAttribute("currentUser", currentUser);

            if (currentUser != null) {
                // Common for both roles: My Groups
                myGroups = grupoService.findGruposByUserId(currentUser.getId());

                if (currentUser.getTipoUsuario() == TipoUsuario.MENTOR) { // Assuming ADMIN/MENTOR can see requests
                    // For Admins: Show pending requests
                    pendingSolicitacoes = grupoService.findAllPendingSolicitacoes();
                    model.addAttribute("pendingSolicitacoes", pendingSolicitacoes);
                    // Admins don't need "explore groups" in this view, they manage requests
                    exploreGroups = new ArrayList<>(); // Ensure it's empty for admin view
                } else { // For Students/Regular Users: Show explore groups
                    exploreGroups = grupoService.findGruposNotJoinedByUserId(currentUser.getId());
                }
            }
        } else {
            // No user logged in: show all groups in explore (default behavior)
            exploreGroups = grupoRepository.findAll();
        }

        model.addAttribute("myGroups", myGroups);
        model.addAttribute("exploreGroups", exploreGroups); // Will be empty for Admins
        // model.addAttribute("pendingSolicitacoes", pendingSolicitacoes); // Already added inside if(MENTOR) block

        return "listar_grupos";
    }

    @GetMapping("/criar_grupo")
    public String criarGrupo(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User criador = userService.getUserByUsername(username);

        if (criador == null || criador.getTipoUsuario() != TipoUsuario.MENTOR) { // Only MENTOR can create
            throw new AccessDeniedException("Apenas mentores podem criar grupos.");
        }

        model.addAttribute("grupo", new Grupo());
        return "criar_grupo";
    }

    @PostMapping("/criar_grupo")
    public String salvarGrupo(@ModelAttribute Grupo grupo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User criador = userService.getUserByUsername(username);

        if (criador == null || criador.getTipoUsuario() != TipoUsuario.MENTOR) {
            throw new AccessDeniedException("Apenas mentores podem criar grupos.");
        }

        grupoService.salvarComCriador(grupo, criador);
        return "redirect:/grupo/listar";
    }

    // Modified endpoint for requesting access (for students)
    @PostMapping("/solicitar_acesso/{id}")
    public String solicitarAcesso(@PathVariable("id") Long grupoId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = auth.getName();
            currentUser = userService.getUserByUsername(username);
        }

        if (currentUser != null && currentUser.getTipoUsuario() != TipoUsuario.MENTOR) { // Only students/non-mentors request
            grupoService.solicitarAcesso(grupoId, currentUser.getId());
        } else {
            // Handle error: mentor trying to request, or not logged in
            return "redirect:/login?error=accessDenied"; // Or specific error page
        }
        return "redirect:/grupo/listar";
    }

    // New endpoint for ADMINS to accept requests
    @PostMapping("/aceitar_solicitacao/{solicitacaoId}")
    public String aceitarSolicitacao(@PathVariable("solicitacaoId") Long solicitacaoId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User adminUser = null;

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = auth.getName();
            adminUser = userService.getUserByUsername(username);
        }

        if (adminUser != null && adminUser.getTipoUsuario() == TipoUsuario.MENTOR) { // Only admins can accept
            grupoService.aceitarSolicitacao(solicitacaoId, adminUser);
        } else {
            throw new AccessDeniedException("Apenas administradores podem aceitar solicitações.");
        }
        return "redirect:/grupo/listar";
    }

    // New endpoint for ADMINS to reject requests
    @PostMapping("/recusar_solicitacao/{solicitacaoId}")
    public String recusarSolicitacao(@PathVariable("solicitacaoId") Long solicitacaoId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User adminUser = null;

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = auth.getName();
            adminUser = userService.getUserByUsername(username);
        }

        if (adminUser != null && adminUser.getTipoUsuario() == TipoUsuario.MENTOR) { // Only admins can reject
            grupoService.recusarSolicitacao(solicitacaoId, adminUser);
        } else {
            throw new AccessDeniedException("Apenas administradores podem recusar solicitações.");
        }
        return "redirect:/grupo/listar";
    }

    // Keep existing /entrar/{id} if it's used elsewhere for direct entry
    // But for students requesting, they use /solicitar_acesso/{id}
    @PostMapping("/entrar/{id}")
    public String entrarGrupo(@PathVariable("id") Long grupoId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = auth.getName();
            currentUser = userService.getUserByUsername(username);
        }

        if (currentUser != null) {
            grupoService.entrarGrupo(grupoId, currentUser.getId());
        } else {
            return "redirect:/login?error=notLoggedIn";
        }
        return "redirect:/grupo/listar";
    }

    // NEW ENDPOINT TO FETCH GROUP MEMBERS (No change from last step)
    @GetMapping("/{id}/membros")
    @ResponseBody
    public ResponseEntity<List<MemberDto>> getGroupMembers(@PathVariable("id") Long grupoId) {
        Optional<Grupo> optionalGrupo = grupoRepository.findById(grupoId);
        if (optionalGrupo.isPresent()) {
            Grupo grupo = optionalGrupo.get();
            List<MemberDto> members = grupo.getMembros().stream()
                    .map(MemberDto::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(members);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}