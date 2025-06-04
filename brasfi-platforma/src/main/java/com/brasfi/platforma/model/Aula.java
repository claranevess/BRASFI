package com.brasfi.platforma.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList; // Importe ArrayList
import java.util.List;
import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "concluida")
    private boolean concluida = false;

    @ManyToOne
    @JoinColumn(name = "administrador_id")
    private Administrador administrador;

    @ManyToMany(mappedBy = "aulas")
    private List<Trilha> trilhas = new ArrayList<>();

    // TO-DO: adicionar o atributo de comentarios

    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @OneToMany(mappedBy = "aula", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Material> materiais = new ArrayList<>();
}