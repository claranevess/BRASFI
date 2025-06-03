package com.brasfi.platforma.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Mensagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    private LocalDate dataCriacao;
    private String message;
}
