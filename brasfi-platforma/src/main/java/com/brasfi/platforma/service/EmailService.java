package com.brasfi.platforma.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendCodeEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("brasficonecta@gmail.com"); // o mesmo do application.properties
        message.setTo(toEmail);
        message.setSubject("Seu Código de Verificação");
        message.setText("Olá!\n\nSeu código de verificação é: " + code + "\n\nObrigado!");
        mailSender.send(message);
    }
}