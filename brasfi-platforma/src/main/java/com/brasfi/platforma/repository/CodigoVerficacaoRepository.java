package com.brasfi.platforma.repository;

import com.brasfi.platforma.model.CodigoVerificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodigoVerficacaoRepository extends JpaRepository<CodigoVerificacao, Long> {

    // Método para buscar um código por email e o próprio código
    Optional<CodigoVerificacao> findByEmailAndCode(String email, String code);

    // Opcional: buscar o código mais recente não usado para um email
    Optional<CodigoVerificacao> findTopByEmailAndUsedFalseOrderByCreatedAtDesc(String email);
}