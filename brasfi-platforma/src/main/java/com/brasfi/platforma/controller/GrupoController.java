package com.brasfi.platforma.controller;

import com.brasfi.platforma.dto.MemberDto;
import com.brasfi.platforma.model.Grupo;
import com.brasfi.platforma.model.User;
import com.brasfi.platforma.model.TipoUsuario;
import com.brasfi.platforma.model.SolicitacaoAcesso;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 

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
        List<SolicitacaoAcesso> pendingSolicitacoes = new ArrayList<>();

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = auth.getName();
            currentUser = userService.getUserByUsername(username);
            model.addAttribute("currentUser", currentUser);

            if (currentUser != null) {
                myGroups = grupoService.findGruposByUserId(currentUser.getId());

                if (currentUser.getTipoUsuario() == TipoUsuario.MENTOR) {
                    pendingSolicitacoes = grupoService.findAllPendingSolicitacoes();
                    model.addAttribute("pendingSolicitacoes", pendingSolicitacoes);
                    exploreGroups = new ArrayList<>();
                } else {
                    exploreGroups = grupoService.findGruposNotJoinedByUserId(currentUser.getId());
                }
            }
        } else {
            exploreGroups = grupoRepository.findAll();
        }

        model.addAttribute("myGroups", myGroups);
        model.addAttribute("exploreGroups", exploreGroups);

        return "listar_grupos";
    }

    @GetMapping("/criar_grupo")
    public String criarGrupo(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User criador = userService.getUserByUsername(username);

        if (criador == null || criador.getTipoUsuario() != TipoUsuario.MENTOR) {
            throw new AccessDeniedException("Apenas mentores podem criar grupos.");
        }

        model.addAttribute("grupo", new Grupo());
        return "criar_grupo";
    }

    @PostMapping("/criar_grupo")
    public String salvarGrupo(@ModelAttribute Grupo grupo, RedirectAttributes redirectAttributes) { 
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User criador = userService.getUserByUsername(username);

        if (criador == null || criador.getTipoUsuario() != TipoUsuario.MENTOR) {
            throw new AccessDeniedException("Apenas mentores podem criar grupos.");
        }

        try {
            grupoService.salvarComCriador(grupo, criador);
            redirectAttributes.addFlashAttribute("successMessage", "Grupo criado com sucesso!"); 
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar grupo: " + e.getMessage()); 
            System.err.println("Erro ao criar grupo: " + e.getMessage());
        }

        return "redirect:/grupo/listar";
    }

    @PostMapping("/solicitar_acesso/{id}")
    public String solicitarAcesso(@PathVariable("id") Long grupoId,
                                  RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = auth.getName();
            currentUser = userService.getUserByUsername(username);
        }

        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para solicitar acesso.");
            return "redirect:/login";
        }

        if (currentUser.getTipoUsuario() == TipoUsuario.MENTOR) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mentores não solicitam acesso a grupos dessa forma.");
            return "redirect:/grupo/listar";
        }

        try {
            grupoService.solicitarAcesso(grupoId, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Sua solicitação de acesso foi enviada com sucesso!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocorreu um erro inesperado ao enviar sua solicitação.");
            System.err.println("Error soliciting access for group " + grupoId + " by user " + currentUser.getId() + ": " + e.getMessage());
        }
        return "redirect:/grupo/listar";
    }

    @PostMapping("/aceitar_solicitacao/{solicitacaoId}")
    public String aceitarSolicitacao(@PathVariable("solicitacaoId") Long solicitacaoId, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User adminUser = null;

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = auth.getName();
            adminUser = userService.getUserByUsername(username);
        }

        if (adminUser == null || adminUser.getTipoUsuario() != TipoUsuario.MENTOR) {
            throw new AccessDeniedException("Apenas mentores podem aceitar solicitações.");
        }

        try {
            grupoService.aceitarSolicitacao(solicitacaoId, adminUser);
            redirectAttributes.addFlashAttribute("successMessage", "Solicitação aceita com sucesso!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao aceitar solicitação.");
            System.err.println("Erro ao aceitar solicitação " + solicitacaoId + ": " + e.getMessage());
        }
        return "redirect:/grupo/listar";
    }

    @PostMapping("/recusar_solicitacao/{solicitacaoId}")
    public String recusarSolicitacao(@PathVariable("solicitacaoId") Long solicitacaoId, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User adminUser = null;

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = auth.getName();
            adminUser = userService.getUserByUsername(username);
        }

        if (adminUser == null || adminUser.getTipoUsuario() != TipoUsuario.MENTOR) {
            throw new AccessDeniedException("Apenas mentores podem recusar solicitações.");
        }

        try {
            grupoService.recusarSolicitacao(solicitacaoId, adminUser);
            redirectAttributes.addFlashAttribute("successMessage", "Solicitação recusada com sucesso!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao recusar solicitação.");
            System.err.println("Erro ao recusar solicitação " + solicitacaoId + ": " + e.getMessage());
        }
        return "redirect:/grupo/listar";
    }

    @PostMapping("/entrar/{id}")
    public String entrarGrupo(@PathVariable("id") Long grupoId, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = auth.getName();
            currentUser = userService.getUserByUsername(username);
        }

        if (currentUser != null) {
            try {
                grupoService.entrarGrupo(grupoId, currentUser.getId());
                redirectAttributes.addFlashAttribute("successMessage", "Você entrou no grupo com sucesso!");
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Erro ao entrar no grupo: " + e.getMessage());
                System.err.println("Error entering group " + grupoId + " by user " + currentUser.getId() + ": " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para entrar em um grupo.");
            return "redirect:/login?error=notLoggedIn";
        }
        return "redirect:/grupo/listar";
    }

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