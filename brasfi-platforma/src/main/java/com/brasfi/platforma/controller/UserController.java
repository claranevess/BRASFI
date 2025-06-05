package com.brasfi.platforma.controller;

import com.brasfi.platforma.model.Estudante;
import com.brasfi.platforma.model.Administrador;
import com.brasfi.platforma.model.TipoUsuario;
import com.brasfi.platforma.model.User;
import com.brasfi.platforma.service.UserService;
import com.brasfi.platforma.service.GeradorCodigoService; // Importar o serviço de geração de código
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam; // Para capturar o código

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private GeradorCodigoService geradorCodigoService; // Injetar o serviço

    @GetMapping("/verificacao")
    public String showVerificationPage() {
        return "user/gerarCodigo";
    }

    // Rota para exibir o formulário de registro
    @GetMapping("/registrar")
    public String mostrarRegistroForm(Model model) {
        model.addAttribute("user", new User());
        return "user/registrarUser";
    }

    @PostMapping("/registrar")
    public String registrarUser(User user, Model model) {
        userService.salvarUserComSenhaCriptografada(user); // Use the method that hashes the password
        model.addAttribute("user", user);
        return "redirect:/escolherCargo?userId=" + user.getId();
    }

    @GetMapping("/escolherCargo")
    public String mostrarEscolhaCargo(@RequestParam Long userId, Model model) {
        // Busca o usuário recém-criado pelo ID
        User user = userService.findById(userId); // Você precisará de um método findById no seu UserService
        model.addAttribute("user", user);
        return "user/escolherCargo";
    }

    @PostMapping("/escolherCargo")
    public String processarEscolhaCargo(@RequestParam Long userId, @RequestParam String tipoUsuario, Model model) {
        User user = userService.findById(userId);

        if (user == null) {
            return "redirect:/registrar";
        }

        TipoUsuario tipo = TipoUsuario.valueOf(tipoUsuario.toUpperCase());

        if (tipo == TipoUsuario.ESTUDANTE) {
            // Update the user's type without re-hashing the password
            userService.atualizarTipoUsuario(userId, TipoUsuario.ESTUDANTE.name()); // Pass the name as string
            // Consider if you need to persist the Estudante entity here, not just instantiate it
            // If Estudante needs to be saved to its own table, you'll need an EstudanteService
            // For now, based on your previous code, you just instantiate it, which won't save it.
            // If `new Estudante(user)` is meant to save it, you'll need an EstudanteRepository and service.
            // Assuming `new Estudante(user);` was just for conceptual representation.
            return "redirect:/";
        } else if (tipo == TipoUsuario.ADMINISTRADOR) {
            // Update the user's type without re-hashing the password
            userService.atualizarTipoUsuario(userId, TipoUsuario.ADMINISTRADOR.name()); // Pass the name as string
            return "redirect:/validarCodigo?userId=" + user.getId();
        }
        return "redirect:/escolherCargo?userId=" + user.getId();
    }

    @GetMapping("/validarCodigo")
    public String mostrarValidacaoCodigo(@RequestParam Long userId, @RequestParam(required = false) String error, Model model) {
        User user = userService.findById(userId);
        if (user == null) {
            return "redirect:/registrar"; // Tratar erro: usuário não encontrado
        }
        model.addAttribute("user", user);
        if (error != null) {
            model.addAttribute("errorMessage", "Código inválido ou expirado. Tente novamente.");
        }
        return "user/validarCodigo";
    }

    @PostMapping("/validarCodigo")
    public String validarCodigo(@RequestParam Long userId, @RequestParam String enteredCode, Model model) {
        User user = userService.findById(userId);
        if (user == null) {
            return "redirect:/registrar";
        }

        String email = user.getEmail();

        boolean isValid = geradorCodigoService.validateCode(email, enteredCode);

        if (isValid) {
            // If this is the final step for ADMIN and you just updated tipo_usuario previously,
            // you might not need to save the user again here.
            // If `new Administrador(user)` is meant to persist, you'll need an AdministradorService.
            return "redirect:/"; // Código válido, redireciona para a home
        } else {
            return "redirect:/validarCodigo?userId=" + userId + "&error=true";
        }
    }

}