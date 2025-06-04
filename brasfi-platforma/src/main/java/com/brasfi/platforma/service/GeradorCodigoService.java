package com.brasfi.platforma.service;

import com.brasfi.platforma.model.CodigoVerificacao;
import com.brasfi.platforma.repository.CodigoVerficacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class GeradorCodigoService {

    private static final int CODE_LENGTH = 6;
    private static final long EXPIRATION_HOURS = 48; // Defina o tempo de expiração em horas

    @Autowired
    private CodigoVerficacaoRepository codigoVerficacaoRepository;

    public String generateAndSaveRandomCode(String email) {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10)); // Gera um dígito de 0 a 9
        }
        String generatedCode = code.toString();

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusHours(EXPIRATION_HOURS); // Define a expiração

        CodigoVerificacao verificationCode = new CodigoVerificacao(email, generatedCode, createdAt, expiresAt);
        codigoVerficacaoRepository.save(verificationCode); // Salva no banco de dados

        return generatedCode;
    }

    // Novo método para validar o código
    public boolean validateCode(String email, String code) {
        Optional<CodigoVerificacao> storedCodeOpt = codigoVerficacaoRepository.findByEmailAndCode(email, code);

        if (storedCodeOpt.isEmpty()) {
            return false; // Código não encontrado
        }

        CodigoVerificacao storedCode = storedCodeOpt.get();

        // 1. Verifica se o código não foi usado (opcional, mas recomendado)
        if (storedCode.isUsed()) {
            return false;
        }

        // 2. Verifica se o código não expirou
        if (LocalDateTime.now().isAfter(storedCode.getExpiresAt())) {
            // Opcional: marcar como usado ou remover o código expirado
            // storedCode.setUsed(true);
            // verificationCodeRepository.save(storedCode);
            return false; // Código expirado
        }

        // Se chegou aqui, o código é válido. Marque como usado para evitar reutilização.
        storedCode.setUsed(true);
        codigoVerficacaoRepository.save(storedCode);

        return true;
    }
}