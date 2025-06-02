package com.brasfi.platforma.service;

import com.brasfi.platforma.model.Grupo;
import com.brasfi.platforma.model.User;
import com.brasfi.platforma.repository.GrupoRepository;
import com.brasfi.platforma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GrupoService {
    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UserRepository userRepository; // suposição que exista

    public void entrarGrupo(Long grupoId, Long usuarioId) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));
        User user = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (grupo.getMembros() == null) {
            grupo.setMembros(new ArrayList<>());
        }

        if (!grupo.getMembros().contains(user)) {
            grupo.getMembros().add(user);
            grupoRepository.save(grupo);
        }
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com email: " + email));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com username: " + username));
    }


    public void salvarComCriador(Grupo grupo, User criador) {
        if (grupo.getMembros() == null) {
            grupo.setMembros(new ArrayList<>());
        }
        if (!grupo.getMembros().contains(criador)) {
            grupo.getMembros().add(criador);
        }
        if (grupo.getAdminsId() == null) {
            grupo.setAdminsId(new ArrayList<>());
        }
        if (!grupo.getAdminsId().contains(criador.getId())) {
            grupo.getAdminsId().add(criador.getId());
        }
        grupoRepository.save(grupo);
    }
}