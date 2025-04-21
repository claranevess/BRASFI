package com.brasfi.platforma.model;

import jakarta.persistence.*;
import lombok.*;

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

    private String password;

    @Enumerated(EnumType.STRING) // garante que o valor seja salvo como texto no db, e não como numero.
    private TipoUsuario tipoUsuario; //Professor ou Estudante

    @Embedded
    private Endereco endereco; // atributo composto: cep, estado, cidade, pais e rua
}
