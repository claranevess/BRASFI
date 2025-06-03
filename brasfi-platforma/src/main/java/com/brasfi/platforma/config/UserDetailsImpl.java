package com.brasfi.platforma.config;

import com.brasfi.platforma.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

    private final User user; // O seu objeto User personalizado

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    // Método para acessar o seu objeto User original
    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna as autoridades (papéis) do usuário.
        // Assumindo que seu User tem um campo 'tipoUsuario' e que você quer mapeá-lo para um papel.
        // Por exemplo, "ADMINISTRADOR" pode se tornar "ROLE_ADMINISTRADOR".
        // É uma boa prática prefixar com "ROLE_" para usar com hasRole() no Spring Security.
        String roleName = "ROLE_" + user.getTipoUsuario().toString().toUpperCase(); // Converte para maiúsculas e adiciona ROLE_
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return user.getSenha(); // Senha do seu objeto User
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // Nome de usuário do seu objeto User
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implemente a lógica real se tiver expiração de conta
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implemente a lógica real se tiver bloqueio de conta
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implemente a lógica real se tiver expiração de credenciais
    }

    @Override
    public boolean isEnabled() {
        return true; // Implemente a lógica real se tiver habilitação de conta
    }
}