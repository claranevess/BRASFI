package com.brasfi.platforma.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User autor;

    private LocalDate dataCriacao;

    @ManyToOne
    @JoinColumn(name = "forum_id")
    private Forum forum;

    // TO-DO: adicionar o atributo de comentarios

    // TO-DO: adicionar o atributo de curtidas
}
