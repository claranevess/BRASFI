package com.brasfi.platforma.service;

import com.brasfi.platforma.model.Trilha;
import com.brasfi.platforma.repository.TrilhaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrilhaService {
    @Autowired
    private TrilhaRepository trilhaRepository;

    public Trilha buscarPorId(Long id) {
        return trilhaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trilha não encontrada com ID: " + id));
    }

    public Trilha salvarTrilha(Trilha trilha) {
        return trilhaRepository.save(trilha);
    }

    public void deletarTrilha(Trilha trilha) {
        Trilha existente = trilhaRepository.findById(trilha.getId())
                .orElseThrow(() -> new IllegalArgumentException("Trilha não encontrada"));
        trilhaRepository.delete(existente);
    }

    public Trilha atualizarTrilha(Trilha trilha) {
        if (!trilhaRepository.existsById(trilha.getId())) {
            throw new IllegalArgumentException("Trilha não encontrada para atualização");
        }
        return trilhaRepository.save(trilha);
    }

}
