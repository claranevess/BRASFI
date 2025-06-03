package com.brasfi.platforma.dto;

import com.brasfi.platforma.model.User; // Assuming User is in model package

public class MemberDto {
    private Long id;
    private String nome; // Assuming User has a 'nome' field
    private String email; // Or any other field you want to display

    public MemberDto(User user) {
        this.id = user.getId();
        this.nome = user.getUsername(); // Or user.getNome() if you have a separate name field
        this.email = user.getEmail(); // Assuming User has an email field
    }

    // Getters and setters (or use Lombok @Data)
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
