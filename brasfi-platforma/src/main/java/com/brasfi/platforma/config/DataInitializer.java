package com.brasfi.platforma.config;

import com.brasfi.platforma.model.User;
import com.brasfi.platforma.model.TipoUsuario;
import com.brasfi.platforma.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminUsername = "admin";
        String adminEmail = "admin@brasfi.com";
        String adminPassword = "123";

        // 1. Verifica se o usuário administrador já existe usando o método do UserService
        Optional<User> existingAdmin = userService.findByUsername(adminUsername);

        if (existingAdmin.isEmpty()) {
            System.out.println("Criando usuário administrador padrão...");

            User adminUser = new User();
            adminUser.setNome("Administrador BRASFI");
            adminUser.setUsername(adminUsername);
            adminUser.setEmail(adminEmail);
            adminUser.setSenha(adminPassword); // senha será criptografada pelo salvarUserComSenhaCriptografada
            adminUser.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
            adminUser.setTelefone("81999999999");

            userService.salvarUserComSenhaCriptografada(adminUser);

            System.out.println("Usuário administrador '" + adminUsername + "' criado com sucesso!");
        } else {
            System.out.println("Usuário administrador '" + adminUsername + "' já existe. Nenhuma ação necessária.");
        }
    }
}