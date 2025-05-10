package com.brasfi.platforma.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String senha;

    @Enumerated(EnumType.STRING) // garante que o valor seja salvo como texto no db, e não como numero.
    private TipoUsuario tipoUsuario; //Professor ou Estudante

    @Embedded
    private Endereco endereco; // atributo composto: cep, estado, cidade, pais e rua

    @ManyToMany(mappedBy = "inscritos")
    private List<Trilha> trilhasInscritas; //  não é obrigatório, mas é útil caso queira saber em quais trilhas um usuário está inscrito.

}
