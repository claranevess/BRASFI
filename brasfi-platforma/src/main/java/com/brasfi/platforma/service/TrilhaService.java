package com.brasfi.platforma.service;

import com.brasfi.platforma.model.Trilha;
import com.brasfi.platforma.model.Video;
import com.brasfi.platforma.repository.TrilhaRepository;
import com.brasfi.platforma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrilhaService {
    @Autowired
    private TrilhaRepository trilhaRepository;

    public Trilha salvarTrilha(Trilha trilha) {
        return trilhaRepository.save(trilha);
    }

}
