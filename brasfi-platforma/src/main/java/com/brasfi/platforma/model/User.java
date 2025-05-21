package com.brasfi.platforma.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.processing.Pattern;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED) // define herança JPA
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    private String nome;

    @Email
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String telefone; // TO-DO: Adicionar pattern

    private String senha;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;

    @Embedded
    private Endereco endereco;

    @ManyToMany(mappedBy = "inscritos")
    private List<Trilha> trilhasInscritas;
}
