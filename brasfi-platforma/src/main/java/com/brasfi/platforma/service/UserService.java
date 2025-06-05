package com.brasfi.platforma.service;

import com.brasfi.platforma.model.User;
import com.brasfi.platforma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void salvarUserComSenhaCriptografada(User user) {

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void salvarUser(User user) {
        user.setSenha(passwordEncoder.encode(user.getSenha())); // criptografa a senha
        userRepository.save(user);
    }

    public User atualizarTipoUsuario(Long userId, String tipoUsuario) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setTipoUsuario(com.brasfi.platforma.model.TipoUsuario.valueOf(tipoUsuario.toUpperCase()));
            return userRepository.save(user);
        }
        return null;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com username: " + username));
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElse(null);
    }
}