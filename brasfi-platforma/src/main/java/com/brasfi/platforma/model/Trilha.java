package com.brasfi.platforma.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trilhas")
public class Trilha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String capa;

    @Enumerated(EnumType.STRING)
    private EixoTematico eixoTematico;

    private Double duracao;

    private String topicosDeAprendizado;

    private String descricao;

    @ManyToMany
    @JoinTable(
            name = "trilha_aula",
            joinColumns = @JoinColumn(name = "trilha_id"),
            inverseJoinColumns = @JoinColumn(name = "aula_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Aula> aulas;

    @ManyToMany
    @JoinTable(
            name = "trilha_user",
            joinColumns = @JoinColumn(name = "trilha_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> inscritos = new HashSet<>();


}
