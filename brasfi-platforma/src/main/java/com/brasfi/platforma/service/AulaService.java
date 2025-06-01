package com.brasfi.platforma.service;

import com.brasfi.platforma.model.Aula;
import com.brasfi.platforma.repository.AulaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AulaService {
    private final AulaRepository aulaRepository;

    public Aula salvarAula(Aula aula) {
        return aulaRepository.save(aula);
    }
}