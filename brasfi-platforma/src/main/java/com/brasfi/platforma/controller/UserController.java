package com.brasfi.platforma.controller;

import com.brasfi.platforma.model.Estudante;
import com.brasfi.platforma.model.Mentor;
import com.brasfi.platforma.model.TipoUsuario;
import com.brasfi.platforma.model.User;
import com.brasfi.platforma.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // Rota para exibir o formulário de registro
    @GetMapping("/registrar")
    public String mostrarRegistroForm(Model model) {
        model.addAttribute("user", new User()); // Adiciona um objeto "user" vazio para o formulário
        return "user/registrarUser"; // Retorna a página de registro
    }

    // Rota para processar o envio do formulário
    @PostMapping("/registrar")
    public String registrarUser(User user) {
        //userService.salvarUser(user); // Salva o usuário no banco de dados
        if(user.getTipoUsuario() == TipoUsuario.ESTUDANTE){
            Estudante estudante = new Estudante(user);
            userService.salvarUser(estudante);
        } else if (user.getTipoUsuario() == TipoUsuario.MENTOR){
            Mentor mentor = new Mentor(user);
            userService.salvarUser(mentor);
        }
        return "redirect:/escolherCargo"; // Redireciona para a página de login após o registro
    }

    @GetMapping("/escolherCargo")
    public String mostrarEscolhaCargo(Model model) {
        return "user/escolherCargo";
    }

    @GetMapping("/validarCodigo")
    public String mostrarValidacaoCodigo(Model model) {
        model.addAttribute("user", new User());
        return "user/validarCodigo";
    }

    @PostMapping("/validarCodigo")
    public String validarCodigo(Model model) {
        model.addAttribute("user", new User());
        return "user/validarCodigo";
    }






}
