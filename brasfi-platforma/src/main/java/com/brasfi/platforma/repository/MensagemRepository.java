package com.brasfi.platforma.repository;

import com.brasfi.platforma.model.Mensagem;
import com.brasfi.platforma.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    List<Mensagem> findByGrupoOrderByDataCriacaoAsc(Grupo grupo);
}

