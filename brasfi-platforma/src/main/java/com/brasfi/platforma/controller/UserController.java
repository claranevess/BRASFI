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
        userService.salvarUserComSenhaCriptografada(user);
        model.addAttribute("user", user);
        return "redirect:/escolherCargo?userId=" + user.getId();
    }

    @GetMapping("/escolherCargo")
    public String mostrarEscolhaCargo(@RequestParam Long userId, Model model) {
        // Busca o usuário recém-criado pelo ID
        User user = userService.findById(userId);
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
            userService.atualizarTipoUsuario(userId, TipoUsuario.ESTUDANTE.name());
            return "redirect:/";
        } else if (tipo == TipoUsuario.ADMINISTRADOR) {
            userService.atualizarTipoUsuario(userId, TipoUsuario.ADMINISTRADOR.name());


            String emailDoAdmin = user.getEmail(); // Get the admin's email
            geradorCodigoService.generateAndSaveRandomCode(emailDoAdmin);
            System.out.println("==== UserController: Código de verificação gerado para o administrador " + emailDoAdmin + " ====");

            return "redirect:/validarCodigo?userId=" + user.getId();
        }
        return "redirect:/escolherCargo?userId=" + user.getId();
    }

    @GetMapping("/validarCodigo")
    public String mostrarValidacaoCodigo(@RequestParam Long userId, @RequestParam(required = false) String error, Model model) {
        User user = userService.findById(userId);
        System.out.println("!!!!!!!! GET VALIDAR CODIGO !!!!!!!");
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
            System.out.println("==== UserController: Usuário NÃO encontrado com ID: " + userId + " ====");
            return "redirect:/registrar";
        }

        String emailDoUsuario = user.getEmail(); // Pega o email do user que está logado ou sendo validado

        System.out.println("==== UserController: Tentando validar código ====");
        System.out.println("==== UserId: " + userId + " ====");
        System.out.println("==== Email do Usuário (da tabela users): '" + emailDoUsuario + "' ====");
        System.out.println("==== Código Digitado: '" + enteredCode + "' ====");

        boolean isValid = geradorCodigoService.validateCode(emailDoUsuario, enteredCode);
        System.out.println("==== UserController: Resultado da validação (isValid): " + isValid + " ====");

        if (isValid) {
            return "redirect:/"; // Código válido
        } else {
            return "redirect:/validarCodigo?userId=" + userId + "&error=true";
        }
    }



}