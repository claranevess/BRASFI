package com.brasfi.platforma.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Curtida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long targetId;

    private LocalDate data;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User autor;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
