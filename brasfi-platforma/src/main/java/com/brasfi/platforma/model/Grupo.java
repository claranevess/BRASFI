package com.brasfi.platforma.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "grupos")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @ManyToMany
    @JoinTable(
            name = "grupo_membros",
            joinColumns = @JoinColumn(name = "grupo_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<User> membros;

    @ElementCollection
    @CollectionTable(name = "grupo_admins", joinColumns = @JoinColumn(name = "grupo_id"))
    @Column(name = "admin_id")
    private List<Long> adminsId;

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> mensagens;

    public void entrarGrupo(Long usuarioID, User user) {
        if (!membros.contains(user)) {
            membros.add(user);
        }
    }
}

