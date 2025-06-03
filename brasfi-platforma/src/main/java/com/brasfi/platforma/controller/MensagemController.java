package com.brasfi.platforma.controller;

import com.brasfi.platforma.model.Mensagem;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat")
public class MensagemController {
    public Mensagem sendMessage(Mensagem message){
        return message;
    }
}