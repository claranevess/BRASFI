package com.brasfi.platforma.service;

import com.brasfi.platforma.model.Aula;
import com.brasfi.platforma.repository.AulaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AulaService {
    private final AulaRepository aulaRepository;

    public Aula salvarAula(Aula aula) {
        return aulaRepository.save(aula);
    }

    public Aula buscarPorId(Long id) {
        return aulaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aula não encontrada com ID: " + id));
    }

    public List<Aula> listarTodas() {
        return aulaRepository.findAll();
    }
    public boolean marcarComoConcluida(Long id) {
        Optional<Aula> optionalAula = aulaRepository.findById(id);
        if (optionalAula.isPresent()) {
            Aula aula = optionalAula.get();
            aula.setConcluida(true);
            aulaRepository.save(aula);
            return true;
        }
        return false;
    }

}
