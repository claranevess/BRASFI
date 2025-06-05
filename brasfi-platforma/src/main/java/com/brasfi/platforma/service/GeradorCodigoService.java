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
        System.out.println("==== Código Gerado ====");
        System.out.println("==== Código : " + verificationCode + "====");

        return generatedCode;
    }

    public boolean validateCode(String email, String code) {
        System.out.println("==== GeradorCodigoService: validateCode chamado com Email: '" + email + "', Código: '" + code + "' ====");
        Optional<CodigoVerificacao> storedCodeOpt = codigoVerficacaoRepository.findByEmailAndCode(email, code);

        if (storedCodeOpt.isEmpty()) {
            System.out.println("==== GeradorCodigoService: Código NÃO ENCONTRADO no banco para Email: '" + email + "' e Código: '" + code + "' ====");
            return false; // Código não encontrado
        }

        CodigoVerificacao storedCode = storedCodeOpt.get();

        if (storedCode.isUsed()) {
            System.out.println("==== GeradorCodigoService: Código já foi USADO para Email: '" + email + "', Código: '" + code + "' ====");
            return false;
        }

        if (LocalDateTime.now().isAfter(storedCode.getExpiresAt())) {
            System.out.println("==== GeradorCodigoService: Código EXPIRADO para Email: '" + email + "', Código: '" + code + "' (Expira em: " + storedCode.getExpiresAt() + ", Agora: " + LocalDateTime.now() + ") ====");
            return false; // Código expirado
        }

        storedCode.setUsed(true);
        codigoVerficacaoRepository.save(storedCode);
        System.out.println("==== GeradorCodigoService: Código VALIDADO com SUCESSO para Email: '" + email + "', Código: '" + code + "' ====");
        return true;
    }
}