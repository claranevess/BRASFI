package com.brasfi.platforma.repository;

import com.brasfi.platforma.model.SolicitacaoAcesso;
import com.brasfi.platforma.model.Grupo;
import com.brasfi.platforma.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SolicitacaoAcessoRepository extends JpaRepository<SolicitacaoAcesso, Long> {

    List<SolicitacaoAcesso> findByGrupoAndStatus(Grupo grupo, SolicitacaoAcesso.StatusSolicitacao status);

    Optional<SolicitacaoAcesso> findByGrupoAndSolicitanteAndStatus(Grupo grupo, User solicitante, SolicitacaoAcesso.StatusSolicitacao status);

    List<SolicitacaoAcesso> findByStatus(SolicitacaoAcesso.StatusSolicitacao status);
}