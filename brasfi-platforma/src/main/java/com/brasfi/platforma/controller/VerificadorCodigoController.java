package com.brasfi.platforma.controller;

import com.brasfi.platforma.service.GeradorCodigoService;
import com.brasfi.platforma.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification")
@CrossOrigin(origins = "http://localhost:8080") // Altere para a URL do seu frontend se for diferente
public class VerificadorCodigoController {

    @Autowired
    private GeradorCodigoService geradorCodigoService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-code")
    public ResponseEntity<String> sendVerificationCode(@RequestParam String email) {
        try {
            // Gera e salva o código no banco de dados
            String code = geradorCodigoService.generateAndSaveRandomCode(email);
            emailService.sendCodeEmail(email, code);
            return ResponseEntity.ok("Código enviado com sucesso para " + email + " (válido por 48 horas)");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao enviar o código: " + e.getMessage());
        }
    }

    // Novo endpoint para validar o código
    @PostMapping("/validate-code")
    public ResponseEntity<String> validateVerificationCode(@RequestParam String email, @RequestParam String code) {
        try {
            boolean isValid = geradorCodigoService.validateCode(email, code);
            System.out.println("==== Código Input: " + code + "====");
            if (isValid) {
                return ResponseEntity.ok("Código validado com sucesso!");
            } else {
                return ResponseEntity.badRequest().body("Código inválido ou expirado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao validar o código: " + e.getMessage());
        }
    }
}