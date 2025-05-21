package com.brasfi.platforma.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estudante extends User {

    @ManyToMany
    private List<Aula> aulasAssistidas;

    public Estudante(User user) {
        this.setId(user.getId());
        this.setUsername(user.getUsername());
        this.setNome(user.getNome());
        this.setEmail(user.getEmail());
        this.setTelefone(user.getTelefone());
        this.setSenha(user.getSenha());
        this.setTipoUsuario(user.getTipoUsuario());
        this.setEndereco(user.getEndereco());
        this.setTrilhasInscritas(user.getTrilhasInscritas());
    }

}
