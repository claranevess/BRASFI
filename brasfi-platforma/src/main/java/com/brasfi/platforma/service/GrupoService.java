package com.brasfi.platforma.service;

import com.brasfi.platforma.model.Grupo;
import com.brasfi.platforma.model.User;
import com.brasfi.platforma.repository.GrupoRepository;
import com.brasfi.platforma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GrupoService {
    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UserRepository userRepository; // Ainda necessário para 'entrarGrupo' pois busca o User pelo ID

    @Transactional
    public void entrarGrupo(Long grupoId, Long usuarioId) {
        Optional<Grupo> optionalGrupo = grupoRepository.findById(grupoId);
        Optional<User> optionalUser = userRepository.findById(usuarioId); // Fetch User entity

        if (optionalGrupo.isPresent() && optionalUser.isPresent()) {
            Grupo grupo = optionalGrupo.get();
            User user = optionalUser.get();

            // Check if the user is already a member to prevent duplicates
            // É bom verificar se 'membros' não é null antes de usar stream
            if (grupo.getMembros() == null) {
                grupo.setMembros(new ArrayList<>());
            }
            boolean isMember = grupo.getMembros().stream()
                    .anyMatch(membro -> membro.getId().equals(user.getId()));
            if (!isMember) {
                grupo.getMembros().add(user);
                grupoRepository.save(grupo);
            }
        } else {
            // Handle case where group or user is not found (e.g., throw exception, log error)
            throw new RuntimeException("Grupo or User not found for ID: " + grupoId + ", " + usuarioId);
        }
    }


    @Transactional
    public void salvarComCriador(Grupo grupo, User criador) {
        // Inicializa a lista de membros se for null
        if (grupo.getMembros() == null) {
            grupo.setMembros(new ArrayList<>());
        }
        // Adiciona o criador como membro se ainda não for
        boolean isCreatorMember = grupo.getMembros().stream()
                .anyMatch(membro -> membro.getId().equals(criador.getId()));
        if (!isCreatorMember) {
            grupo.getMembros().add(criador);
        }

        // Inicializa a lista de adminsId se for null
        if (grupo.getAdminsId() == null) {
            grupo.setAdminsId(new ArrayList<>());
        }
        // Adiciona o criador como admin se ainda não for
        if (!grupo.getAdminsId().contains(criador.getId())) {
            grupo.getAdminsId().add(criador.getId());
        }
        grupoRepository.save(grupo);
    }

    public List<Grupo> findGruposByUserId(Long userId) {
        // Isso busca grupos onde o ID do membro corresponde ao userId.
        // O `findByMembros_Id` é um método do Spring Data JPA que funciona bem com relacionamentos ManyToMany.
        return grupoRepository.findByMembros_Id(userId);
    }

    public List<Grupo> findGruposNotJoinedByUserId(Long userId) {
        // Get all groups
        List<Grupo> allGrupos = grupoRepository.findAll();

        // Filter out groups where the user is already a member
        return allGrupos.stream()
                .filter(grupo -> {
                    // Garante que a coleção de membros não seja nula antes de usar stream
                    if (grupo.getMembros() == null) {
                        return true; // Se não tem membros, o usuário certamente não está nele
                    }
                    return grupo.getMembros().stream()
                            .noneMatch(membro -> membro.getId().equals(userId));
                })
                .collect(Collectors.toList());
    }
}