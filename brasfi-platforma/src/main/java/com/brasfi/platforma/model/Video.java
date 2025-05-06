package com.brasfi.platforma.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String titulo;
    private String categoria;

    @ElementCollection
    private List<String> comunidade; // Comunidade agora é uma lista

    private String usuario; // Tipo de dado do usuário

    // Método para verificar se o usuário existe
    public String verificarUsuario(String usuario) {
        return this.usuario != null && this.usuario.equals(usuario) ? "Usuário existe" : "Usuário não encontrado";
    }

    // Método para verificar se a comunidade existe
    public String verificarComunidade(String comunidade) {
        return this.comunidade != null && this.comunidade.contains(comunidade) ? "Comunidade existe" : "Comunidade não encontrada";
    }
}