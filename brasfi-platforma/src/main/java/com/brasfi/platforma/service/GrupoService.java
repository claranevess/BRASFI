package com.brasfi.platforma.service;

import com.brasfi.platforma.model.Grupo;
import com.brasfi.platforma.model.User;
import com.brasfi.platforma.model.SolicitacaoAcesso;
import com.brasfi.platforma.repository.GrupoRepository;
import com.brasfi.platforma.repository.UserRepository;
import com.brasfi.platforma.repository.SolicitacaoAcessoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SolicitacaoAcessoRepository solicitacaoAcessoRepository;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // Método salvarComCriador (já existente no seu código)
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
    public Grupo salvarComCriadorEMembros(Grupo grupo, User criador, List<Long> membrosIds) {
        if (grupo.getMembros() == null) {
            grupo.setMembros(new ArrayList<>());
        }
        if (grupo.getMembros().stream().noneMatch(membro -> membro.getId().equals(criador.getId()))) {
            grupo.getMembros().add(criador);
        }
        if (grupo.getAdminsId() == null) {
            grupo.setAdminsId(new ArrayList<>());
        }
        if (!grupo.getAdminsId().contains(criador.getId())) {
            grupo.getAdminsId().add(criador.getId());
        }

        if (membrosIds != null && !membrosIds.isEmpty()) {
            for (Long membroId : membrosIds) {
                if (!membroId.equals(criador.getId())) {
                    User membroParaAdicionar = userRepository.findById(membroId)
                            .orElseThrow(() -> new RuntimeException("Usuário com ID " + membroId + " não encontrado ao tentar adicionar ao grupo."));
                    if (grupo.getMembros().stream().noneMatch(m -> m.getId().equals(membroParaAdicionar.getId()))) {
                        grupo.getMembros().add(membroParaAdicionar);
                    }
                }
            }
        }
        return grupoRepository.save(grupo); // Retorna o grupo salvo (útil para pegar o ID)
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

                Optional<SolicitacaoAcesso> pendingRequest = solicitacaoAcessoRepository.findByGrupoAndSolicitanteAndStatus(grupo, user, SolicitacaoAcesso.StatusSolicitacao.PENDENTE);
                pendingRequest.ifPresent(req -> {
                    req.setStatus(SolicitacaoAcesso.StatusSolicitacao.ACEITA);
                    req.setDataProcessamento(LocalDateTime.now());
                    solicitacaoAcessoRepository.save(req);
                });
            }
        } else {
            throw new RuntimeException("Grupo ou Usuário não encontrado para ID: " + grupoId + ", " + usuarioId);
        }
    }

    @Transactional
    public void solicitarAcesso(Long grupoId, Long solicitanteId) {
        Optional<Grupo> optionalGrupo = grupoRepository.findById(grupoId);
        Optional<User> optionalSolicitante = userRepository.findById(solicitanteId);

        if (optionalGrupo.isPresent() && optionalSolicitante.isPresent()) {
            Grupo grupo = optionalGrupo.get();
            User solicitante = optionalSolicitante.get();

            boolean isMember = grupo.getMembros().stream()
                    .anyMatch(membro -> membro.getId().equals(solicitante.getId()));
            if (isMember) {
                throw new IllegalStateException("Usuário já é membro deste grupo.");
            }

            Optional<SolicitacaoAcesso> existingRequest = solicitacaoAcessoRepository.findByGrupoAndSolicitanteAndStatus(grupo, solicitante, SolicitacaoAcesso.StatusSolicitacao.PENDENTE);
            if (existingRequest.isPresent()) {
                throw new IllegalStateException("Você já tem uma solicitação de acesso pendente para este grupo!");
            }

            SolicitacaoAcesso novaSolicitacao = new SolicitacaoAcesso();
            novaSolicitacao.setGrupo(grupo);
            novaSolicitacao.setSolicitante(solicitante);
            novaSolicitacao.setStatus(SolicitacaoAcesso.StatusSolicitacao.PENDENTE);
            novaSolicitacao.setDataSolicitacao(LocalDateTime.now()); // É bom registrar a data da solicitação
            solicitacaoAcessoRepository.save(novaSolicitacao);
        } else {
            throw new RuntimeException("Grupo ou Solicitante não encontrado.");
        }
    }

    @Transactional
    public void aceitarSolicitacao(Long solicitacaoId, User adminProcessador) {
        Optional<SolicitacaoAcesso> optionalSolicitacao = solicitacaoAcessoRepository.findById(solicitacaoId);
        if (optionalSolicitacao.isPresent()) {
            SolicitacaoAcesso solicitacao = optionalSolicitacao.get();
            if (solicitacao.getStatus() == SolicitacaoAcesso.StatusSolicitacao.PENDENTE) {
                entrarGrupo(solicitacao.getGrupo().getId(), solicitacao.getSolicitante().getId());

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

    public Optional<Grupo> findById(Long id) {
        return grupoRepository.findById(id);
    }

    public List<Grupo> findGruposNotJoinedByUserId(Long userId) {
        List<Grupo> allGrupos = grupoRepository.findAll();
        if (userId == null) {
            return allGrupos;
        }
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

    public List<SolicitacaoAcesso> findAllPendingSolicitacoes() {
        return solicitacaoAcessoRepository.findByStatus(SolicitacaoAcesso.StatusSolicitacao.PENDENTE);
    }
}