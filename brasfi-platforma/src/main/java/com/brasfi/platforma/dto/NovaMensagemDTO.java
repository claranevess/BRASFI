package com.brasfi.platforma.dto;

public record NovaMensagemDTO(
        Long userId,
        Long grupoId,
        String texto
) {}
