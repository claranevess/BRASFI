package com.brasfi.platforma.service;

import com.brasfi.platforma.model.User;
import com.brasfi.platforma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // salvar o usuario no banco de dados
    public void salvarUser(User user) {
        userRepository.save(user);
    }
}
