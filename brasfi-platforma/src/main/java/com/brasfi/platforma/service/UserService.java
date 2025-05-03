package com.brasfi.platforma.service;

import com.brasfi.platforma.model.User;
import com.brasfi.platforma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void salvarUser(User user) {
        user.setSenha(passwordEncoder.encode(user.getSenha())); // criptografa a senha
        userRepository.save(user);
    }
}
