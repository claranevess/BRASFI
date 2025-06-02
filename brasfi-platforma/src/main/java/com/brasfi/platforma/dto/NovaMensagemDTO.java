package com.brasfi.platforma.dto;

import java.time.LocalDateTime;

public class NovaMensagemDTO {
    private Long userId;
    private Long grupoId;
    private String texto;
    private LocalDateTime dataCriacao;

    public NovaMensagemDTO() {
    }

    public NovaMensagemDTO(Long userId, Long grupoId, String texto, LocalDateTime dataCriacao) {
        this.userId = userId;
        this.grupoId = grupoId;
        this.texto = texto;
        this.dataCriacao = dataCriacao;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}

