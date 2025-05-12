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

public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String texto;

    private LocalDate dataCriacao;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}