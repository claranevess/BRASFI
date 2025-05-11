package com.brasfi.platforma.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mentor extends User {

    private String avisos;

    @OneToMany
    private List<Aula> aulasPublicadas;
}
