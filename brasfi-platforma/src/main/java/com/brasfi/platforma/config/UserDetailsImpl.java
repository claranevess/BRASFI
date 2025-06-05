package com.brasfi.platforma.config;

import com.brasfi.platforma.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getTipoUsuario() != null) {
            String roleName = "ROLE_" + user.getTipoUsuario().toString().toUpperCase();
            return Collections.singletonList(new SimpleGrantedAuthority(roleName));
        } else {
            // Se o TipoUsuario for nulo, isso indica um problema de dados ou lógica de registro.
            // Para evitar NullPointerException durante a autenticação, podemos retornar uma role padrão
            // ou logar um erro e retornar uma lista vazia/role de 'usuário não atribuído'.
            System.err.println("ATENÇÃO: Usuário " + user.getUsername() + " possui TipoUsuario nulo. Atribuindo ROLE_DEFAULT.");
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_DEFAULT")); // OU Collections.emptyList();
        }
    }

    @Override
    public String getPassword() {
        return user.getSenha();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
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
        return true;
    }
}