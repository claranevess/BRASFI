package com.brasfi.platforma.config;

import com.brasfi.platforma.model.TipoUsuario;
import com.brasfi.platforma.model.User;
import com.brasfi.platforma.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    public CustomSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        if (user.getTipoUsuario() == TipoUsuario.ADMINISTRADOR) {
            response.sendRedirect("/avisos/dashboard/adm");
        } else {
            response.sendRedirect("/avisos/dashboard/estudante");
        }
    }
}

