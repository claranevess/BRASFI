package com.brasfi.platforma.controller;

import com.brasfi.platforma.model.TipoUsuario;
import com.brasfi.platforma.model.User;
import com.brasfi.platforma.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.Optional; // Import Optional

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        User currentUser = null;

        // --- START: Getting the currently authenticated user (Using Spring Security) ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated and not an anonymous user
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            String username = authentication.getName(); // This typically returns the username (or email, if configured)

            // Try to find the user by username (or email, depending on your UserDetails implementation)
            Optional<User> userOptional = userService.findByUsername(username); // Use findByUsername
            if (userOptional.isPresent()) {
                currentUser = userOptional.get();
            } else {
                // Log an error: User found in authentication but not in DB.
                // This scenario might indicate an issue with user persistence or retrieval.
                System.err.println("Error: Authenticated user '" + username + "' not found in the database.");
            }
        }
        // --- END: Getting the currently authenticated user ---


        // --- START: FOR DEMONSTRATION/TESTING PURPOSES ONLY (REMOVE IN PRODUCTION) ---
        // If no user is authenticated (e.g., during development without full security setup)
        // or if the authenticated user wasn't found in DB, use a dummy user for display.
        if (currentUser == null) {
            // Attempt to fetch a pre-existing user (e.g., with ID 1) for testing
            currentUser = userService.findById(1L);

            if (currentUser == null) {
                // If user with ID 1 doesn't exist, create a generic dummy user
                // This is purely for ensuring the page loads with some data during development.
                // In a real application, if no user is found, you might redirect to login.
                currentUser = new User();
                currentUser.setId(99L); // Assign a dummy ID
                currentUser.setNome("Visitante Dev");
                currentUser.setEmail("dev@example.com");
                // Ensure getType() handles null gracefully or set a default.
                // If TipoUsuario.ESTUDANTE is the default, set it.
                currentUser.setTipoUsuario(TipoUsuario.ESTUDANTE); // Using the enum directly
            }
        }
        // --- END: FOR DEMONSTRATION/TESTING PURPOSES ONLY ---


        if (currentUser != null) {
            model.addAttribute("userName", currentUser.getNome());
            // Make sure getType() returns the string representation like "ESTUDANTE" or "ADMINISTRADOR"
            model.addAttribute("userType", currentUser.getTipoUsuario().name()); // Assuming getTipoUsuario() returns TipoUsuario enum
        } else {
            // Ultimate fallback if no user could be determined
            model.addAttribute("userName", "Usuário Anônimo");
            model.addAttribute("userType", "Desconhecido");
        }

        return "dashboard";
    }
}