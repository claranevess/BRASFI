package com.brasfi.platforma.dto;

import com.brasfi.platforma.model.User;

public class MemberDto {
    private Long id;
    private String nome;
    private String email;

    public MemberDto(User user) {
        this.id = user.getId();
        this.nome = user.getUsername();
        this.email = user.getEmail();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
