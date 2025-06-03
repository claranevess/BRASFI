// src/main/java/com/brasfi/platforma/service/GrupoService.java
package com.brasfi.platforma.service;

import com.brasfi.platforma.model.Grupo;
import com.brasfi.platforma.model.User;
import com.brasfi.platforma.model.SolicitacaoAcesso; // Import new entity
import com.brasfi.platforma.repository.GrupoRepository;
import com.brasfi.platforma.repository.UserRepository;
import com.brasfi.platforma.repository.SolicitacaoAcessoRepository; // Import new repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList; // Added this import

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SolicitacaoAcessoRepository solicitacaoAcessoRepository; // Inject new repository

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null); // Use orElse(null) for Optional
    }

    @Transactional
    public void salvarComCriador(Grupo grupo, User criador) {
        if (grupo.getMembros() == null) {
            grupo.setMembros(new ArrayList<>());
        }
        boolean isCreatorMember = grupo.getMembros().stream()
                .anyMatch(membro -> membro.getId().equals(criador.getId()));
        if (!isCreatorMember) {
            grupo.getMembros().add(criador);
        }

        if (grupo.getAdminsId() == null) {
            grupo.setAdminsId(new ArrayList<>());
        }
        if (!grupo.getAdminsId().contains(criador.getId())) {
            grupo.getAdminsId().add(criador.getId());
        }
        grupoRepository.save(grupo);
    }

    @Transactional
    public void entrarGrupo(Long grupoId, Long usuarioId) {
        Optional<Grupo> optionalGrupo = grupoRepository.findById(grupoId);
        Optional<User> optionalUser = userRepository.findById(usuarioId);

        if (optionalGrupo.isPresent() && optionalUser.isPresent()) {
            Grupo grupo = optionalGrupo.get();
            User user = optionalUser.get();

            boolean isMember = grupo.getMembros().stream()
                    .anyMatch(membro -> membro.getId().equals(user.getId()));
            if (!isMember) {
                grupo.getMembros().add(user);
                grupoRepository.save(grupo);

                // If accepted via a request, update the request status
                Optional<SolicitacaoAcesso> pendingRequest = solicitacaoAcessoRepository.findByGrupoAndSolicitanteAndStatus(grupo, user, SolicitacaoAcesso.StatusSolicitacao.PENDENTE);
                pendingRequest.ifPresent(req -> {
                    req.setStatus(SolicitacaoAcesso.StatusSolicitacao.ACEITA);
                    req.setDataProcessamento(LocalDateTime.now());
                    solicitacaoAcessoRepository.save(req);
                });
            }
        } else {
            throw new RuntimeException("Grupo or User not found for ID: " + grupoId + ", " + usuarioId);
        }
    }

    // New method to handle access request submission
    @Transactional
    public void solicitarAcesso(Long grupoId, Long solicitanteId) {
        Optional<Grupo> optionalGrupo = grupoRepository.findById(grupoId);
        Optional<User> optionalSolicitante = userRepository.findById(solicitanteId);

        if (optionalGrupo.isPresent() && optionalSolicitante.isPresent()) {
            Grupo grupo = optionalGrupo.get();
            User solicitante = optionalSolicitante.get();

            // Check if user is already a member
            boolean isMember = grupo.getMembros().stream()
                    .anyMatch(membro -> membro.getId().equals(solicitante.getId()));
            if (isMember) {
                throw new IllegalStateException("Usuário já é membro deste grupo.");
            }

            // Check if there's already a pending request from this user for this group
            Optional<SolicitacaoAcesso> existingRequest = solicitacaoAcessoRepository.findByGrupoAndSolicitanteAndStatus(grupo, solicitante, SolicitacaoAcesso.StatusSolicitacao.PENDENTE);
            if (existingRequest.isPresent()) {
                throw new IllegalStateException("Solicitação de acesso já pendente para este grupo.");
            }

            SolicitacaoAcesso novaSolicitacao = new SolicitacaoAcesso();
            novaSolicitacao.setGrupo(grupo);
            novaSolicitacao.setSolicitante(solicitante);
            novaSolicitacao.setStatus(SolicitacaoAcesso.StatusSolicitacao.PENDENTE);
            solicitacaoAcessoRepository.save(novaSolicitacao);
        } else {
            throw new RuntimeException("Grupo or Solicitante not found.");
        }
    }

    // New method for admins to accept a request
    @Transactional
    public void aceitarSolicitacao(Long solicitacaoId, User adminProcessador) {
        Optional<SolicitacaoAcesso> optionalSolicitacao = solicitacaoAcessoRepository.findById(solicitacaoId);
        if (optionalSolicitacao.isPresent()) {
            SolicitacaoAcesso solicitacao = optionalSolicitacao.get();
            if (solicitacao.getStatus() == SolicitacaoAcesso.StatusSolicitacao.PENDENTE) {
                // Add the user to the group
                entrarGrupo(solicitacao.getGrupo().getId(), solicitacao.getSolicitante().getId());

                // Update request status
                solicitacao.setStatus(SolicitacaoAcesso.StatusSolicitacao.ACEITA);
                solicitacao.setDataProcessamento(LocalDateTime.now());
                solicitacao.setAdminProcessador(adminProcessador);
                solicitacaoAcessoRepository.save(solicitacao);
            } else {
                throw new IllegalStateException("Solicitação não está pendente ou já foi processada.");
            }
        } else {
            throw new RuntimeException("Solicitação de acesso não encontrada.");
        }
    }

    // New method for admins to reject a request
    @Transactional
    public void recusarSolicitacao(Long solicitacaoId, User adminProcessador) {
        Optional<SolicitacaoAcesso> optionalSolicitacao = solicitacaoAcessoRepository.findById(solicitacaoId);
        if (optionalSolicitacao.isPresent()) {
            SolicitacaoAcesso solicitacao = optionalSolicitacao.get();
            if (solicitacao.getStatus() == SolicitacaoAcesso.StatusSolicitacao.PENDENTE) {
                solicitacao.setStatus(SolicitacaoAcesso.StatusSolicitacao.RECUSADA);
                solicitacao.setDataProcessamento(LocalDateTime.now());
                solicitacao.setAdminProcessador(adminProcessador);
                solicitacaoAcessoRepository.save(solicitacao);
            } else {
                throw new IllegalStateException("Solicitação não está pendente ou já foi processada.");
            }
        } else {
            throw new RuntimeException("Solicitação de acesso não encontrada.");
        }
    }


    public List<Grupo> findGruposByUserId(Long userId) {
        return grupoRepository.findByMembros_Id(userId);
    }

    public List<Grupo> findGruposNotJoinedByUserId(Long userId) {
        List<Grupo> allGrupos = grupoRepository.findAll();
        return allGrupos.stream()
                .filter(grupo -> {
                    if (grupo.getMembros() == null) {
                        return true;
                    }
                    return grupo.getMembros().stream()
                            .noneMatch(membro -> membro.getId().equals(userId));
                })
                .collect(Collectors.toList());
    }

    // New method to find all pending requests for display in admin view
    public List<SolicitacaoAcesso> findAllPendingSolicitacoes() {
        return solicitacaoAcessoRepository.findByStatus(SolicitacaoAcesso.StatusSolicitacao.PENDENTE);
    }
}