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
    @SendTo("/topic/chat")
    public ChatOutput newMessage(ChatInput input) {
        String escapedUser = HtmlUtils.htmlEscape(input.user());
        String escapedMessage = HtmlUtils.htmlEscape(input.message());
        return new ChatOutput(escapedUser + ": " + escapedMessage);
    }

    public record ChatInput(String user, String message) {}
    public record ChatOutput(String content) {}
}