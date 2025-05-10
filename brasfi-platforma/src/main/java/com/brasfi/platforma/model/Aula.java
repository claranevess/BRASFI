package com.brasfi.platforma.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "aulas")
public class Aula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String link;
    private String descricao;
    private String professor; // TO-DO: alterar para tipo Professor

    @ManyToMany(mappedBy = "aulas")
    private List<Trilha> trilhas;

    // TO-DO: adicionar o atributo de comentarios

}
