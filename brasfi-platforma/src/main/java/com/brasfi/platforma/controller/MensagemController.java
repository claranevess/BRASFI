package com.brasfi.platforma.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat")
public class MensagemController {

    @GetMapping
    public String exibirChat() {
        return "mensagem";
    }
}
