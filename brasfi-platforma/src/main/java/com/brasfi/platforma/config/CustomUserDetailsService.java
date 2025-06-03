package com.brasfi.platforma.config;

import com.brasfi.platforma.model.User;
import com.brasfi.platforma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

        return new UserDetailsImpl(user);
    }
}