package com.brasfi.platforma.controller;

import com.brasfi.platforma.dto.MemberDto; // Certifique-se que este DTO existe e é usado corretamente
import com.brasfi.platforma.model.Grupo;
import com.brasfi.platforma.model.User;
import com.brasfi.platforma.model.TipoUsuario;
import com.brasfi.platforma.model.SolicitacaoAcesso;
// Removido GrupoRepository se não for usado diretamente
// import com.brasfi.platforma.repository.GrupoRepository;
import com.brasfi.platforma.repository.GrupoRepository;
import com.brasfi.platforma.service.GrupoService;
import com.brasfi.platforma.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
// Removido SecurityContextHolder se Authentication é injetado
// import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
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
    
    public static class CriarGrupoModalDto {
        private String nome;
        private String descricao;
        private List<Long> membrosIds;

        // Getters
        public String getNome() { return nome; }
        public String getDescricao() { return descricao; }
        public List<Long> getMembrosIds() { return membrosIds; }

        // Setters
        public void setNome(String nome) { this.nome = nome; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        public void setMembrosIds(List<Long> membrosIds) { this.membrosIds = membrosIds; }
    }

    @GetMapping("/listar")
    public String listarGrupos(Model model, Authentication authentication){
        User currentUser = null;
        List<Grupo> myGroups = new ArrayList<>();
        List<Grupo> exploreGroups = new ArrayList<>();
        List<SolicitacaoAcesso> pendingSolicitacoes = new ArrayList<>();
        List<User> allUsersForModal = Collections.emptyList();

        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            String username = authentication.getName();
            currentUser = userService.getUserByUsername(username);
            model.addAttribute("currentUser", currentUser);

            if (currentUser != null) {
                myGroups = grupoService.findGruposByUserId(currentUser.getId());

                if (currentUser.getTipoUsuario() == TipoUsuario.ADMINISTRADOR) {
                    pendingSolicitacoes = grupoService.findAllPendingSolicitacoes();
                    allUsersForModal = userService.findAllUsers();
                } else {
                    exploreGroups = grupoService.findGruposNotJoinedByUserId(currentUser.getId());
                }
            }
        } else {
            exploreGroups = grupoService.findGruposNotJoinedByUserId(null);
        }

        model.addAttribute("myGroups", myGroups);
        model.addAttribute("exploreGroups", exploreGroups);
        model.addAttribute("pendingSolicitacoes", pendingSolicitacoes);
        model.addAttribute("allUsersForModal", allUsersForModal);
        model.addAttribute("newGrupoDto", new CriarGrupoModalDto());

        return "listar_grupos";
    }

    @PostMapping("/criar_grupo_modal")
    @ResponseBody
    public ResponseEntity<?> salvarGrupoViaModal(@RequestBody CriarGrupoModalDto grupoDto, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Usuário não autenticado."));
        }
        String username = authentication.getName();
        User criador = userService.getUserByUsername(username);

        if (criador == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Detalhes do usuário criador não encontrados."));
        }

        if (criador.getTipoUsuario() != TipoUsuario.ADMINISTRADOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Apenas administradores podem criar grupos."));
        }
        if (grupoDto.getNome() == null || grupoDto.getNome().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "O nome do grupo é obrigatório."));
        }

        Grupo grupo = new Grupo();
        grupo.setNome(grupoDto.getNome().trim());
        grupo.setDescricao(grupoDto.getDescricao() != null ? grupoDto.getDescricao().trim() : null);

        try {
            Grupo grupoSalvo = grupoService.salvarComCriadorEMembros(grupo, criador, grupoDto.getMembrosIds());
            return ResponseEntity.ok(Map.of("message", "Grupo '" + grupoSalvo.getNome() + "' criado com sucesso!", "grupoId", grupoSalvo.getId()));
        } catch (RuntimeException e) {
            System.err.println("Erro ao criar grupo via modal: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro interno ao tentar criar o grupo: " + e.getMessage()));
        }
    }

    @PostMapping("/solicitar_acesso/{id}")
    public String solicitarAcesso(@PathVariable("id") Long grupoId,
                                  RedirectAttributes redirectAttributes, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para solicitar acesso.");
            return "redirect:/login";
        }
        User currentUser = userService.getUserByUsername(authentication.getName());
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuário não encontrado.");
            return "redirect:/login";
        }

        if (currentUser.getTipoUsuario() == TipoUsuario.ADMINISTRADOR) {
            redirectAttributes.addFlashAttribute("errorMessage", "Administradores não solicitam acesso a grupos dessa forma.");
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
    public String aceitarSolicitacao(@PathVariable("solicitacaoId") Long solicitacaoId, RedirectAttributes redirectAttributes, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("Acesso negado.");
        }
        User adminUser = userService.getUserByUsername(authentication.getName());
        if (adminUser == null || adminUser.getTipoUsuario() != TipoUsuario.ADMINISTRADOR) {
            throw new AccessDeniedException("Apenas administradores podem aceitar solicitações.");
        }

        try {
            grupoService.aceitarSolicitacao(solicitacaoId, adminUser);
            redirectAttributes.addFlashAttribute("successMessage", "Solicitação aceita com sucesso! Usuário adicionado ao grupo.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao processar a aceitação da solicitação.");
            System.err.println("Erro ao aceitar solicitação " + solicitacaoId + ": " + e.getMessage());
        }
        return "redirect:/grupo/listar";
    }

    @PostMapping("/recusar_solicitacao/{solicitacaoId}")
    public String recusarSolicitacao(@PathVariable("solicitacaoId") Long solicitacaoId, RedirectAttributes redirectAttributes, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("Acesso negado.");
        }
        User adminUser = userService.getUserByUsername(authentication.getName());
        if (adminUser == null || adminUser.getTipoUsuario() != TipoUsuario.ADMINISTRADOR) {
            throw new AccessDeniedException("Apenas administradores podem recusar solicitações.");
        }

        try {
            grupoService.recusarSolicitacao(solicitacaoId, adminUser);
            redirectAttributes.addFlashAttribute("successMessage", "Solicitação recusada com sucesso!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao processar a recusa da solicitação.");
            System.err.println("Erro ao recusar solicitação " + solicitacaoId + ": " + e.getMessage());
        }
        return "redirect:/grupo/listar";
    }

    @PostMapping("/entrar/{id}")
    public String entrarGrupo(@PathVariable("id") Long grupoId, RedirectAttributes redirectAttributes, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você precisa estar logado para entrar em um grupo.");
            return "redirect:/login";
        }
        User currentUser = userService.getUserByUsername(authentication.getName());
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuário não encontrado.");
            return "redirect:/login";
        }

        try {
            grupoService.entrarGrupo(grupoId, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Você entrou no grupo com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao entrar no grupo: " + e.getMessage());
            System.err.println("Error entering group " + grupoId + " by user " + currentUser.getId() + ": " + e.getMessage());
        }
        return "redirect:/grupo/listar";
    }

    @GetMapping("/{id}/membros")
    @ResponseBody
    public ResponseEntity<List<MemberDto>> getGroupMembers(@PathVariable("id") Long grupoId) {
        Optional<Grupo> optionalGrupo = grupoService.findById(grupoId);
        if (optionalGrupo.isPresent()) {
            Grupo grupo = optionalGrupo.get();
            if (grupo.getMembros() == null) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            List<MemberDto> members = grupo.getMembros().stream()
                    .map(user -> new MemberDto(user)) // Passa o objeto User
                    .collect(Collectors.toList());
            return ResponseEntity.ok(members);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}