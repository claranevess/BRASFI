package com.brasfi.platforma.controller;

import com.brasfi.platforma.domain.ChatInput;
import com.brasfi.platforma.domain.ChatOutput;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class MensagemController {

    @MessageMapping("/newMensagem")
    @SendTo("/toppic/chat")
    public ChatOutput newMessage(ChatInput input){
        return new ChatOutput(HtmlUtils.htmlEscape(input.user() + ": " + input.message()));
    }
}