package com.brasfi.platforma.config;

import com.brasfi.platforma.model.User;
import com.brasfi.platforma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(login)
                .or(() -> userRepository.findByEmail(login))
                .or(() -> userRepository.findByTelefone(login))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + login));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getSenha(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

}
