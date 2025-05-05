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
@Table(name = "videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String title;
    private String categoria;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario;

    @ElementCollection
    @CollectionTable(name = "video_comunidades", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "comunidade")
    private List<String> comunidades;

    public boolean usuarioExiste() {
        return this.usuario != null && this.usuario.getId() != null;
    }

    public boolean comunidadeExiste(String comunidade) {
        return this.comunidades != null && this.comunidades.contains(comunidade);
    }
}