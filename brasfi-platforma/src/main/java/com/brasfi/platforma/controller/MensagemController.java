package com.brasfi.platforma.controller;

import com.brasfi.platforma.model.Mensagem;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MensagemController {

    @MessageMapping("/chatMessage")
    @SendTo("/canal")
    public Mensagem sendMessage(Mensagem message){
        return message;
    }
}