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
        super(user.getId(), user.getUsername(), user.getEmail(), user.getSenha(), user.getTipoUsuario(), user.getEndereco(), user.getTrilhasInscritas());
    }

}
