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
}
