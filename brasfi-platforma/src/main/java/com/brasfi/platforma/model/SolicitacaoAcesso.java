package com.brasfi.platforma.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "solicitacoes_acesso")
public class SolicitacaoAcesso {

    public enum StatusSolicitacao {
        PENDENTE, ACEITA, RECUSADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private User solicitante; // The user who requested access

    @Enumerated(EnumType.STRING)
    private StatusSolicitacao status = StatusSolicitacao.PENDENTE; // Default status

    private LocalDateTime dataSolicitacao = LocalDateTime.now();

    // Optional: User who processed the request (admin) and when
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User adminProcessador;

    private LocalDateTime dataProcessamento;
}